package com.elssolution.gridanalysis.modbus;

import com.fazecast.jSerialComm.SerialPort;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class SerialPortWrapperImpl implements SerialPortWrapper {

    private final String portName;
    private final int baudRate;

    @Value("${serial.io.readTimeoutMs:1000}")
    private int readTimeoutMs;

    @Value("${serial.io.writeTimeoutMs:1000}")
    private int writeTimeoutMs;

    private SerialPort serialPort;

    public SerialPortWrapperImpl(String portName, int baudRate) {
        this.portName = portName;
        this.baudRate = baudRate;
    }

    @Override
    public void open() throws IOException {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                readTimeoutMs,
                writeTimeoutMs
        );

        if (!serialPort.openPort()) {
            throw new IOException("Cannot open serial port: " + portName);
        }

        log.info("Serial port opened: {}", portName);
    }

    @Override
    public void close() {
        if (serialPort != null) {
            serialPort.closePort();
            log.info("Serial port closed: {}", portName);
        }
    }

    @Override
    public InputStream getInputStream() {
        return serialPort.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return serialPort.getOutputStream();
    }

    @Override public int getBaudRate() { return baudRate; }
    @Override public int getFlowControlIn() { return SerialPort.FLOW_CONTROL_DISABLED; }
    @Override public int getFlowControlOut() { return SerialPort.FLOW_CONTROL_DISABLED; }
    @Override public int getDataBits() { return 8; }
    @Override public int getStopBits() { return SerialPort.ONE_STOP_BIT; }
    @Override public int getParity() { return SerialPort.NO_PARITY; }
}

