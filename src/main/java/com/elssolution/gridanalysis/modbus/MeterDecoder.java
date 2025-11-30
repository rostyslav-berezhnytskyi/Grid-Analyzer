package com.elssolution.gridanalysis.modbus;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Encodes/decodes IEEE-754 floats to/from Modbus 16-bit register arrays (two words per float).
 */
@Component
public class MeterDecoder {

    public enum WordOrder { BE, LE }

    @Value("${smartmetr.floatOrder:BE}")
    private WordOrder wordOrder;

    private boolean littleEndian;

    @PostConstruct
    void init() {
        this.littleEndian = (wordOrder == WordOrder.LE);
    }

    public float readFloat(short[] words, int wordOffset) {
        int w0 = words[wordOffset]     & 0xFFFF;
        int w1 = words[wordOffset + 1] & 0xFFFF;
        int bits = littleEndian ? ((w1 << 16) | w0) : ((w0 << 16) | w1);
        return Float.intBitsToFloat(bits);
    }

    public float readFloatOrDefault(short[] words, int offset, float fallback) {
        if (words == null || offset < 0 || offset + 1 >= words.length) return fallback;
        return readFloat(words, offset);
    }
}

