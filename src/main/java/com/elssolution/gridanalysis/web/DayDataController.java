package com.elssolution.gridanalysis.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DayDataController {

    @Value("${storage.csv.path}")
    private String basePath;

    @GetMapping("/day")
    public Map<String, Object> getDay(@RequestParam String date) throws Exception {
        File file = new File(basePath + "/" + date + ".csv");

        Map<String, Object> result = new HashMap<>();
        List<String> timestamps = new ArrayList<>();
        List<Float> vL1 = new ArrayList<>(), vL2 = new ArrayList<>(), vL3 = new ArrayList<>();
        List<Float> iL1 = new ArrayList<>(), iL2 = new ArrayList<>(), iL3 = new ArrayList<>();
        List<Float> pL1 = new ArrayList<>(), pL2 = new ArrayList<>(), pL3 = new ArrayList<>();
        List<Float> pTotal = new ArrayList<>();

        if (!file.exists()) {
            result.put("timestamps", timestamps);
            result.put("vL1", vL1);
            result.put("vL2", vL2);
            result.put("vL3", vL3);
            result.put("iL1", iL1);
            result.put("iL2", iL2);
            result.put("iL3", iL3);
            result.put("pL1", pL1);
            result.put("pL2", pL2);
            result.put("pL3", pL3);
            result.put("pTotal", pTotal);
            return result;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // skip HEADER

            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");

                timestamps.add(p[0].substring(11)); // HH:mm:ss

                vL1.add(parse(p[2]));
                vL2.add(parse(p[3]));
                vL3.add(parse(p[4]));

                iL1.add(parse(p[8]));
                iL2.add(parse(p[9]));
                iL3.add(parse(p[10]));

                pL1.add(parse(p[12]));
                pL2.add(parse(p[13]));
                pL3.add(parse(p[14]));
                pTotal.add(parse(p[15]));
            }
        }

        result.put("timestamps", timestamps);
        result.put("vL1", vL1);
        result.put("vL2", vL2);
        result.put("vL3", vL3);

        result.put("iL1", iL1);
        result.put("iL2", iL2);
        result.put("iL3", iL3);

        result.put("pL1", pL1);
        result.put("pL2", pL2);
        result.put("pL3", pL3);
        result.put("pTotal", pTotal);

        return result;
    }

    private float parse(String s) {
        return Float.parseFloat(s.replace(",", "."));
    }
}
