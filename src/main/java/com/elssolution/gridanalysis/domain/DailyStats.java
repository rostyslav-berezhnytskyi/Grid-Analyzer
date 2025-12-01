package com.elssolution.gridanalysis.domain;

public record DailyStats(
        String date,

        // Voltage max/min
        float vL1_max, String vL1_max_time,
        float vL2_max, String vL2_max_time,
        float vL3_max, String vL3_max_time,

        float vL1_min, String vL1_min_time,
        float vL2_min, String vL2_min_time,
        float vL3_min, String vL3_min_time,

        // Voltage imbalance
        float maxImbalance, String imbalance_time,

        // Currents
        float iL1_max, String iL1_max_time,
        float iL2_max, String iL2_max_time,
        float iL3_max, String iL3_max_time,

        // Active Power
        float pL1_max, String pL1_max_time,
        float pL2_max, String pL2_max_time,
        float pL3_max, String pL3_max_time,
        float pTotal_max, String pTotal_max_time,

        // Reactive Power
        float qTotal_max, String qTotal_max_time,

        // Energy totals (snapshot at last write)
        float kwhImportTotal,
        float kvarhImportTotal
) {}

