package com.elssolution.gridanalysis.domain;

/** Immutable meter snapshot: raw words + read timestamp. */
public final class SmSnapshot {
    public final short[] data;
    public final long updatedAtMs;

    public SmSnapshot(short[] data, long updatedAtMs) {
        this.data = data;
        this.updatedAtMs = updatedAtMs;
    }
}

