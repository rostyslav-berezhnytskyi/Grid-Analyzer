package com.elssolution.gridanalysis.storage;

import com.elssolution.gridanalysis.domain.GridMetricsFull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CsvStorageService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CsvStorageService() {
        // ensure data folder exists
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void append(GridMetricsFull m) {
        String filename = "data/" + DF.format(LocalDate.now()) + ".csv";
        File file = new File(filename);

        boolean writeHeader = !file.exists();

        try (FileWriter out = new FileWriter(file, true)) {

            if (writeHeader) {
                out.write(headerLine());
            }

            out.write(formatLine(m));

        } catch (IOException e) {
            System.err.println("CSV write error: " + e.getMessage());
        }
    }

    private String headerLine() {
        return "timestamp,"
                + "vL1,vL2,vL3,vL12,vL23,vL31,"
                + "iL1,iL2,iL3,iNeutral,"
                + "pL1,pL2,pL3,pTotal,"
                + "qL1,qL2,qL3,qTotal,"
                + "sL1,sL2,sL3,sTotal,"
                + "pfL1,pfL2,pfL3,pfTotal,"
                + "frequency,"
                + "kwhImportL1,kwhImportL2,kwhImportL3,kwhImportTotal,"
                + "kvarhImportL1,kvarhImportL2,kvarhImportL3,kvarhImportTotal\n";
    }

    private String formatLine(GridMetricsFull m) {
        return String.format(
                "%d," +
                        "%.2f,%.2f,%.2f,%.2f,%.2f,%.2f," +
                        "%.2f,%.2f,%.2f,%.2f," +
                        "%.2f,%.2f,%.2f,%.2f," +
                        "%.2f,%.2f,%.2f,%.2f," +
                        "%.2f,%.2f,%.2f,%.2f," +
                        "%.2f,%.2f,%.2f,%.2f," +
                        "%.2f," +
                        "%.2f,%.2f,%.2f,%.2f," +
                        "%.2f,%.2f,%.2f,%.2f\n",

                m.timestamp(),

                m.vL1(), m.vL2(), m.vL3(), m.vL12(), m.vL23(), m.vL31(),

                m.iL1(), m.iL2(), m.iL3(), m.iNeutral(),

                m.pL1(), m.pL2(), m.pL3(), m.pTotal(),

                m.qL1(), m.qL2(), m.qL3(), m.qTotal(),

                m.sL1(), m.sL2(), m.sL3(), m.sTotal(),

                m.pfL1(), m.pfL2(), m.pfL3(), m.pfTotal(),

                m.frequency(),

                m.kwhImportL1(), m.kwhImportL2(), m.kwhImportL3(), m.kwhImportTotal(),

                m.kvarhImportL1(), m.kvarhImportL2(), m.kvarhImportL3(), m.kvarhImportTotal()
        );
    }
}
