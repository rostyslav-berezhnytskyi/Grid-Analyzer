package com.elssolution.gridanalysis.domain;

import com.elssolution.gridanalysis.modbus.MeterDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class GridMetricsDecoder {

    private final MeterDecoder f;
    private final MeterRegisterMap r;


    public GridMetricsDecoder(MeterDecoder f, MeterRegisterMap r) {
        this.f = f;
        this.r = r;
    }

    public GridMetricsFull decode(SmSnapshot snap) {
        short[] w = snap.data;

        String human = Instant.ofEpochMilli(snap.updatedAtMs)// normal human time
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new GridMetricsFull(
                human,
                snap.updatedAtMs,

                f.readFloatOrDefault(w, r.vL1(), 0),
                f.readFloatOrDefault(w, r.vL2(), 0),
                f.readFloatOrDefault(w, r.vL3(), 0),
                f.readFloatOrDefault(w, r.vL12(), 0),
                f.readFloatOrDefault(w, r.vL23(), 0),
                f.readFloatOrDefault(w, r.vL31(), 0),

                f.readFloatOrDefault(w, r.iL1(), 0),
                f.readFloatOrDefault(w, r.iL2(), 0),
                f.readFloatOrDefault(w, r.iL3(), 0),
                f.readFloatOrDefault(w, r.in(), 0),

                f.readFloatOrDefault(w, r.pL1(), 0),
                f.readFloatOrDefault(w, r.pL2(), 0),
                f.readFloatOrDefault(w, r.pL3(), 0),
                f.readFloatOrDefault(w, r.pTotal(), 0),

                f.readFloatOrDefault(w, r.qL1(), 0),
                f.readFloatOrDefault(w, r.qL2(), 0),
                f.readFloatOrDefault(w, r.qL3(), 0),
                f.readFloatOrDefault(w, r.qTotal(), 0),

                f.readFloatOrDefault(w, r.sL1(), 0),
                f.readFloatOrDefault(w, r.sL2(), 0),
                f.readFloatOrDefault(w, r.sL3(), 0),
                f.readFloatOrDefault(w, r.sTotal(), 0),

                f.readFloatOrDefault(w, r.pfL1(), 0),
                f.readFloatOrDefault(w, r.pfL2(), 0),
                f.readFloatOrDefault(w, r.pfL3(), 0),
                f.readFloatOrDefault(w, r.pfTotal(), 0),

                f.readFloatOrDefault(w, r.frequency(), 0),

                f.readFloatOrDefault(w, r.kwhImportL1(), 0),
                f.readFloatOrDefault(w, r.kwhImportL2(), 0),
                f.readFloatOrDefault(w, r.kwhImportL3(), 0),
                f.readFloatOrDefault(w, r.kwhImportTotal(), 0),

                f.readFloatOrDefault(w, r.kvarhImportL1(), 0),
                f.readFloatOrDefault(w, r.kvarhImportL2(), 0),
                f.readFloatOrDefault(w, r.kvarhImportL3(), 0),
                f.readFloatOrDefault(w, r.kvarhImportTotal(), 0)
        );
    }
}
