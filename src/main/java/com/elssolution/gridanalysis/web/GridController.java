package com.elssolution.gridanalysis.web;

import com.elssolution.gridanalysis.domain.GridMetricsFull;
import com.elssolution.gridanalysis.modbus.ModbusSmReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@RestController
public class GridController {
    @Value("${storage.csv.path}")
    private String basePath;

    private final ModbusSmReader smReader;

    public GridController(ModbusSmReader smReader) {
        this.smReader = smReader;
    }

    @GetMapping("/api/live")
    public GridMetricsFull live() {
        return smReader.getLatestMetricsFull();
    }

    @GetMapping("/api/days")
    public List<String> days() {
        File folder = new File(basePath);
        return Arrays.stream(folder.list())
                .filter(f -> f.endsWith(".csv"))
                .sorted()
                .toList();
    }

    @GetMapping("/api/history/{day}")
    public String history(@PathVariable String day) throws IOException {
        return Files.readString(Path.of(basePath + "/" + day));
    }
}

