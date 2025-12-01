package com.elssolution.gridanalysis.modbus;

import com.elssolution.gridanalysis.domain.GridMetricsDecoder;
import com.elssolution.gridanalysis.domain.GridMetricsFull;
import com.elssolution.gridanalysis.domain.SmSnapshot;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersResponse;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ModbusSmReader {

    private final ScheduledExecutorService scheduler;
    private final GridMetricsDecoder decoder;

    @Value("${serial.input.port}") private String port;
    @Value("${serial.input.baudRate}") private int baud;
    @Value("${serial.input.slaveId}") private int slaveId;

    @Value("${serial.input.startOffset:0}") private int startOffset;
    @Value("${serial.input.numberOfRegisters:72}") private int numberOfRegisters;
    @Value("${serial.input.pollInterval:1000}") private int pollInterval;

    @Value("${serial.input.initialOpenDelayMs:2000}") private int initialOpenDelayMs;
    @Value("${serial.input.reopenBackoffMs:2000}") private int reopenBackoffMs;

    private volatile ModbusMaster master;

    private volatile SmSnapshot latestSnapshot = new SmSnapshot(new short[0], 0);
    private volatile GridMetricsFull latestDecoded = GridMetricsFull.zero();

    private short[] lastGoodRegisters = null;
    private int staleCounter = 0;

    private static final int MAX_STALE = 20;

    public ModbusSmReader(ScheduledExecutorService scheduler, GridMetricsDecoder decoder) {
        this.scheduler = scheduler;
        this.decoder = decoder;
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleWithFixedDelay(this::tick, initialOpenDelayMs, pollInterval, TimeUnit.MILLISECONDS);
        log.info("ModbusSmReader started");
    }

    @PreDestroy
    public void stop() {
        closeQuiet();
    }

    private void tick() {
        try {
            ensureOpen();

            ReadInputRegistersRequest req =
                    new ReadInputRegistersRequest(slaveId, startOffset, numberOfRegisters);

            ReadInputRegistersResponse resp =
                    (ReadInputRegistersResponse) master.send(req);

            short[] raw = resp.getShortData();

            // 1️⃣ SANITY CHECK: no data read → offline
            if (raw == null || raw.length == 0) {
                markOffline("empty buffer");
                return;
            }

            // 2️⃣ STALE BUFFER DETECTION
            if (lastGoodRegisters != null &&
                    Arrays.equals(raw, lastGoodRegisters)) {

                staleCounter++;

                if (staleCounter >= MAX_STALE) {
                    markOffline("stale cached data");
                    return;
                }
            } else {
                staleCounter = 0;
            }

            // 3️⃣ SAVE GOOD SNAPSHOT
            lastGoodRegisters = Arrays.copyOf(raw, raw.length);
            latestSnapshot = new SmSnapshot(lastGoodRegisters, System.currentTimeMillis());

            // 4️⃣ DECODE
            latestDecoded = decoder.decode(latestSnapshot);

        } catch (Exception ex) {
            markOffline(ex.getMessage());
            closeQuiet();
            sleep(reopenBackoffMs);
        }
    }

    private void markOffline(String reason) {
        latestDecoded = GridMetricsFull.zero();
        latestSnapshot = new SmSnapshot(new short[0], 0);
        log.warn("SM OFFLINE → {}", reason);
    }

    private void ensureOpen() throws Exception {
        if (master != null) return;

        SerialPortWrapper wrapper = new SerialPortWrapperImpl(port, baud);
        ModbusMaster m = new ModbusFactory().createRtuMaster(wrapper);

        m.setTimeout(1000);
        m.setRetries(0);
        m.init();

        master = m;
        log.info("Modbus port opened: {}", port);
    }

    private void closeQuiet() {
        if (master != null) {
            try { master.destroy(); }
            catch (Exception ignored) {}
            master = null;
            log.info("Modbus port closed");
        }
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException ignored) {}
    }

    public GridMetricsFull getLatestMetricsFull() {
        return latestDecoded;
    }

    public SmSnapshot getLatestSnapshotSM() {
        return latestSnapshot;
    }
}
