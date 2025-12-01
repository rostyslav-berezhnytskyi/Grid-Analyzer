package com.elssolution.gridanalysis.service;

import com.elssolution.gridanalysis.domain.DailyStats;
import com.elssolution.gridanalysis.domain.GridMetricsFull;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyStatsService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DailyStats today = null;

    public DailyStatsService() {
        loadToday();
    }

    private void loadToday() {
        String date = DF.format(LocalDate.now());
        File f = new File("data/stats.csv");

        if (!f.exists()) {
            today = createEmpty(date);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(date)) {
                    today = parse(line);
                    return;
                }
            }
        } catch (Exception ignored) {}

        today = createEmpty(date);
    }

    private DailyStats createEmpty(String date) {
        return new DailyStats(
                date,
                // max voltages
                0, "--", 0, "--", 0, "--",
                // min voltages
                1000, "--", 1000, "--", 1000, "--",
                // imbalance
                0, "--",
                // currents
                0, "--", 0, "--", 0, "--",
                // active power
                0, "--", 0, "--", 0, "--", 0, "--",
                // reactive
                0, "--",
                // energy
                0, 0
        );
    }

    public synchronized void update(GridMetricsFull m) {
        // ⛔ Ignore invalid bootstrapped values (1970-01-01 03:00:00)
        if (m.timestamp() < 946684800000L) {
            return;
        }

        String date = DF.format(LocalDate.now());
        String t = m.timestampHuman().substring(11); // HH:mm:ss only

        // If day changed
        if (!today.date().equals(date)) {
            today = createEmpty(date);
        }

        float imbalance = Math.abs(
                Math.max(m.vL1(), Math.max(m.vL2(), m.vL3())) -
                        Math.min(m.vL1(), Math.min(m.vL2(), m.vL3()))
        );

        today = new DailyStats(
                date,

                // MAX VOLTAGES
                max(today.vL1_max(), m.vL1(), today.vL1_max_time(), t).val,
                max(today.vL1_max(), m.vL1(), today.vL1_max_time(), t).time,

                max(today.vL2_max(), m.vL2(), today.vL2_max_time(), t).val,
                max(today.vL2_max(), m.vL2(), today.vL2_max_time(), t).time,

                max(today.vL3_max(), m.vL3(), today.vL3_max_time(), t).val,
                max(today.vL3_max(), m.vL3(), today.vL3_max_time(), t).time,

                // MIN VOLTAGES
                min(today.vL1_min(), m.vL1(), today.vL1_min_time(), t).val,
                min(today.vL1_min(), m.vL1(), today.vL1_min_time(), t).time,

                min(today.vL2_min(), m.vL2(), today.vL2_min_time(), t).val,
                min(today.vL2_min(), m.vL2(), today.vL2_min_time(), t).time,

                min(today.vL3_min(), m.vL3(), today.vL3_min_time(), t).val,
                min(today.vL3_min(), m.vL3(), today.vL3_min_time(), t).time,

                // IMBALANCE
                max(today.maxImbalance(), imbalance, today.imbalance_time(), t).val,
                max(today.maxImbalance(), imbalance, today.imbalance_time(), t).time,

                // CURRENTS
                max(today.iL1_max(), m.iL1(), today.iL1_max_time(), t).val,
                max(today.iL1_max(), m.iL1(), today.iL1_max_time(), t).time,

                max(today.iL2_max(), m.iL2(), today.iL2_max_time(), t).val,
                max(today.iL2_max(), m.iL2(), today.iL2_max_time(), t).time,

                max(today.iL3_max(), m.iL3(), today.iL3_max_time(), t).val,
                max(today.iL3_max(), m.iL3(), today.iL3_max_time(), t).time,

                // ACTIVE POWER
                max(today.pL1_max(), m.pL1(), today.pL1_max_time(), t).val,
                max(today.pL1_max(), m.pL1(), today.pL1_max_time(), t).time,

                max(today.pL2_max(), m.pL2(), today.pL2_max_time(), t).val,
                max(today.pL2_max(), m.pL2(), today.pL2_max_time(), t).time,

                max(today.pL3_max(), m.pL3(), today.pL3_max_time(), t).val,
                max(today.pL3_max(), m.pL3(), today.pL3_max_time(), t).time,

                max(today.pTotal_max(), m.pTotal(), today.pTotal_max_time(), t).val,
                max(today.pTotal_max(), m.pTotal(), today.pTotal_max_time(), t).time,

                // REACTIVE POWER
                max(today.qTotal_max(), m.qTotal(), today.qTotal_max_time(), t).val,
                max(today.qTotal_max(), m.qTotal(), today.qTotal_max_time(), t).time,

                // ENERGY — always update
                m.kwhImportTotal(),
                m.kvarhImportTotal()
        );

        save();
    }

    private static class MaxMin {
        float val;
        String time;
        MaxMin(float v, String t) { val = v; time = t; }
    }

    private MaxMin max(float oldVal, float newVal, String oldTime, String newTime) {
        return (newVal > oldVal) ? new MaxMin(newVal, newTime) : new MaxMin(oldVal, oldTime);
    }

    private MaxMin min(float oldVal, float newVal, String oldTime, String newTime) {
        return (newVal < oldVal) ? new MaxMin(newVal, newTime) : new MaxMin(oldVal, oldTime);
    }

    private synchronized void save() {
        File f = new File("data/stats.csv");
        List<String> lines = new ArrayList<>();

        // HEADER always first line
        lines.add(headerLine().trim());

        // Load old stats (skip header)
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; } // skip old header
                    if (!line.startsWith(today.date())) lines.add(line);
                }
            } catch (Exception ignored) {}
        }

        // Add today
        lines.add(format(today).trim());

        // Write file
        try (FileWriter out = new FileWriter(f, false)) {
            for (String s : lines) {
                out.write(s);
                out.write("\n");
            }
        } catch (Exception ignored) {}
    }


    private String format(DailyStats s) {
        return String.join(";",
                s.date(),

                f(s.vL1_max()), s.vL1_max_time(),
                f(s.vL2_max()), s.vL2_max_time(),
                f(s.vL3_max()), s.vL3_max_time(),

                f(s.vL1_min()), s.vL1_min_time(),
                f(s.vL2_min()), s.vL2_min_time(),
                f(s.vL3_min()), s.vL3_min_time(),

                f(s.maxImbalance()), s.imbalance_time(),

                f(s.iL1_max()), s.iL1_max_time(),
                f(s.iL2_max()), s.iL2_max_time(),
                f(s.iL3_max()), s.iL3_max_time(),

                f(s.pL1_max()), s.pL1_max_time(),
                f(s.pL2_max()), s.pL2_max_time(),
                f(s.pL3_max()), s.pL3_max_time(),
                f(s.pTotal_max()), s.pTotal_max_time(),

                f(s.qTotal_max()), s.qTotal_max_time(),

                f(s.kwhImportTotal()),
                f(s.kvarhImportTotal())
        ) + "\n";
    }

    private String f(float v) {
        return String.format("%.2f", v);
    }


    private DailyStats parse(String line) {
        try {
            String[] p = line.split(";");

            // FIX: convert "221,42" → "221.42"
            for (int i = 0; i < p.length; i++) {
                p[i] = p[i].replace(",", ".");
            }

            int i = 0;

            return new DailyStats(
                    p[i++],   // date

                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],

                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],

                    Float.parseFloat(p[i++]), p[i++],

                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],

                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],
                    Float.parseFloat(p[i++]), p[i++],

                    Float.parseFloat(p[i++]), p[i++],

                    Float.parseFloat(p[i++]),
                    Float.parseFloat(p[i++])
            );

        } catch (Exception ex) {
            System.err.println("Failed to parse DailyStats line: " + line);
            ex.printStackTrace();
            return createEmpty(DF.format(LocalDate.now()));
        }
    }

    public DailyStats loadByDate(String date) {
        File f = new File("data/stats.csv");

        if (!f.exists()) return createEmpty(date);

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(date)) {
                    return parse(line);
                }
            }
        } catch (Exception ignored) {}

        return createEmpty(date);
    }



    private String headerLine() {
        return "date;"
                + "vL1_max;vL1_max_time;"
                + "vL2_max;vL2_max_time;"
                + "vL3_max;vL3_max_time;"
                + "vL1_min;vL1_min_time;"
                + "vL2_min;vL2_min_time;"
                + "vL3_min;vL3_min_time;"
                + "maxImbalance;imbalance_time;"
                + "iL1_max;iL1_max_time;"
                + "iL2_max;iL2_max_time;"
                + "iL3_max;iL3_max_time;"
                + "pL1_max;pL1_max_time;"
                + "pL2_max;pL2_max_time;"
                + "pL3_max;pL3_max_time;"
                + "pTotal_max;pTotal_max_time;"
                + "qTotal_max;qTotal_max_time;"
                + "kwhImportTotal;"
                + "kvarhImportTotal\n";
    }


    public DailyStats getToday() { return today; }
}
