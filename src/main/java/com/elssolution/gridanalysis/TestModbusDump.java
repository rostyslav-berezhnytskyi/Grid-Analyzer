package com.elssolution.gridanalysis;

import com.elssolution.gridanalysis.modbus.SerialPortWrapperImpl;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersResponse;

public class TestModbusDump {

    public static void main(String[] args) throws Exception {

        String port = "COM6";   // change if needed
        int baud = 9600;
        int slaveId = 1;

        ModbusMaster master = new ModbusFactory()
                .createRtuMaster(new SerialPortWrapperImpl(port, baud));

        master.init();
        System.out.println("=== SDM630 EXTENDED REGISTER SCAN ===");
        System.out.println();

        scanBlock(master, slaveId, 0, 72);
        scanBlock(master, slaveId, 100, 100);
        scanBlock(master, slaveId, 200, 100);
        scanBlock(master, slaveId, 300, 100);

        master.destroy();
        System.out.println("=== FINISHED ===");
    }

    private static void scanBlock(ModbusMaster master, int slaveId, int start, int count)
            throws ModbusTransportException {

        System.out.println("\n====== SCANNING BLOCK: " + start + " → " + (start + count) + " ======");

        ReadInputRegistersRequest req =
                new ReadInputRegistersRequest(slaveId, start, count);

        ReadInputRegistersResponse resp =
                (ReadInputRegistersResponse) master.send(req);

        if (resp.isException()) {
            System.out.println("Modbus exception: " + resp.getExceptionMessage());
            return;
        }

        short[] data = resp.getShortData();

        for (int i = 0; i < data.length - 1; i += 2) {
            float f = toFloat(data[i], data[i + 1]);
            System.out.printf("Float @ %03d = %f%n", start + i, f);
        }
    }

    // SDM uses Big-Endian 2×16 → float32
    private static float toFloat(short hi, short lo) {
        int bits = ((hi & 0xffff) << 16) | (lo & 0xffff);
        return Float.intBitsToFloat(bits);
    }
}
