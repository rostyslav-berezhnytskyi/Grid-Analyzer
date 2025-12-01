package com.elssolution.gridanalysis.service;

import com.elssolution.gridanalysis.domain.GridMetricsDecoder;
import com.elssolution.gridanalysis.domain.GridMetricsFull;
import com.elssolution.gridanalysis.domain.SmSnapshot;
import com.elssolution.gridanalysis.modbus.ModbusSmReader;
import com.elssolution.gridanalysis.storage.CsvStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PollingService {

    private final ModbusSmReader modbus;
    private final GridMetricsDecoder decoder;
    private final CsvStorageService csv;
    private final DailyStatsService dailyStatsService;

    // thread for polling
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // latest decoded values available for REST API
    private volatile GridMetricsFull latest;

    public PollingService(ModbusSmReader modbus,
                          GridMetricsDecoder decoder,
                          CsvStorageService csv, DailyStatsService dailyStatsService) {
        this.modbus = modbus;
        this.decoder = decoder;
        this.csv = csv;
        this.dailyStatsService = dailyStatsService;
    }

    @PostConstruct
    public void start() {
        // 1000 ms interval (1 second)
        scheduler.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
        log.info("PollingService started with 1-second interval");
    }

    private void tick() {
        try {
            SmSnapshot snap = modbus.getLatestSnapshotSM();
            if (snap == null) {
                log.warn("No Modbus snapshot available yet");
                return;
            }

            // decode raw snapshot
            GridMetricsFull m = modbus.getLatestMetricsFull();
            latest = m;

            // append to CSV
            csv.append(m);
            dailyStatsService.update(m);

        } catch (Exception e) {
            log.error("Polling tick failed: {}", e.getMessage(), e);
        }
    }

    public GridMetricsFull getLatest() {
        return latest;
    }
}

