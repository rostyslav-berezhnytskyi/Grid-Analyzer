package com.elssolution.gridanalysis.web;

import com.elssolution.gridanalysis.domain.GridMetricsFull;
import com.elssolution.gridanalysis.modbus.ModbusSmReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GridController {

    private final ModbusSmReader smReader;

    public GridController(ModbusSmReader smReader) {
        this.smReader = smReader;
    }

    @GetMapping("/api/live")
    public GridMetricsFull live() {
        return smReader.getLatestMetricsFull();
    }
}

