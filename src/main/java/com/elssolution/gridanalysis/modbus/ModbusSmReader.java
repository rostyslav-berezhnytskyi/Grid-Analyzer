package com.elssolution.gridanalysis.modbus;

import com.elssolution.gridanalysis.domain.SmSnapshot;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersResponse;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ModbusSmReader {

    @Value("${serial.input.port}") private String port;
    @Value("${serial.input.baudRate}") private int baud;
    @Value("${serial.input.slaveId}") private int slaveId;
    @Value("${serial.input.startOffset:0}") private int startOffset;

    @Value("${serial.input.numberOfRegisters:72}") private int numberOfRegisters;
    @Value("${serial.input.pollInterval:1000}") private int pollInterval;

    @Value("${serial.input.initialOpenDelayMs:2000}") private int initialOpenDelayMs;
    @Value("${serial.input.reopenBackoffMs:2000}") private int reopenBackoffMs;
    @Value("${serial.input.warmupMs:2000}") private int warmupMs;
    @Value("${serial.input.timeoutsBeforeReopen:3}") private int timeoutsBeforeReopen;

    private final ScheduledExecutorService scheduler;
    private volatile ModbusMaster master;
    private volatile SmSnapshot latestSnapshot = new SmSnapshot(new short[0], 0);

    private volatile int timeoutCount = 0;
    private volatile long lastOpenAt = 0;

    public ModbusSmReader(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleWithFixedDelay(this::tick, initialOpenDelayMs, pollInterval, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() {
        closeQuiet();
    }

    private void tick() {
        try {
            ensureOpen();

            ReadInputRegistersRequest req = new ReadInputRegistersRequest(
                    slaveId, startOffset, numberOfRegisters
            );
            ReadInputRegistersResponse resp = (ReadInputRegistersResponse) master.send(req);

            if (resp.isException()) {
                throw new RuntimeException("Modbus exception: " + resp.getExceptionMessage());
            }

            timeoutCount = 0;
            latestSnapshot = new SmSnapshot(resp.getShortData(), System.currentTimeMillis());

        } catch (Exception e) {
            handleError(e);
        }
    }

    private void ensureOpen() throws Exception {
        if (master != null) return;

        SerialPortWrapper wrapper = new SerialPortWrapperImpl(port, baud);
        ModbusMaster m = new ModbusFactory().createRtuMaster(wrapper);

        m.setTimeout(1200);
        m.setRetries(0);
        m.init();

        master = m;
        lastOpenAt = System.currentTimeMillis();

        Thread.sleep(200); // settle
        log.info("Modbus port opened: {}", port);
    }

    private void closeQuiet() {
        if (master != null) {
            try { master.destroy(); } catch (Exception ignored) {}
            master = null;
        }
    }

    private void handleError(Exception e) {
        long sinceOpen = System.currentTimeMillis() - lastOpenAt;
        timeoutCount++;

        boolean inWarmup = sinceOpen < warmupMs;
        boolean reopen = timeoutCount >= timeoutsBeforeReopen;

        log.warn("Modbus read error: {}, warmup={}, streak={}", e.getMessage(), inWarmup, timeoutCount);

        if (!inWarmup && reopen) {
            closeQuiet();
            timeoutCount = 0;

            try { Thread.sleep(reopenBackoffMs); } catch (InterruptedException ignored) {}
        }
    }

    public SmSnapshot getLatestSnapshotSM() {
        return latestSnapshot;
    }
}

