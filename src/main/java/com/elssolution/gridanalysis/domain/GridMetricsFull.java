package com.elssolution.gridanalysis.domain;

public record GridMetricsFull(
        String timestampHuman,
        long timestamp,

        float vL1, float vL2, float vL3,
        float vL12, float vL23, float vL31,

        float iL1, float iL2, float iL3, float iNeutral,

        float pL1, float pL2, float pL3, float pTotal,
        float qL1, float qL2, float qL3, float qTotal,
        float sL1, float sL2, float sL3, float sTotal,

        float pfL1, float pfL2, float pfL3, float pfTotal,

        float frequency,

        float kwhImportL1, float kwhImportL2, float kwhImportL3,
        float kwhImportTotal,

        float kvarhImportL1, float kvarhImportL2, float kvarhImportL3,
        float kvarhImportTotal
) {
    public static GridMetricsFull zero() {
        long now = System.currentTimeMillis();
        String human = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new GridMetricsFull(
                human, now,
                0,0,0,0,0,0,       // voltages
                0,0,0,0,           // currents + neutral
                0,0,0,0,           // active power
                0,0,0,0,           // reactive power
                0,0,0,0,           // apparent power
                0,0,0,0,           // power factor
                0,                 // frequency
                0,0,0,0,           // kWh import
                0,0,0,0            // kvarh import
        );
    }

}
