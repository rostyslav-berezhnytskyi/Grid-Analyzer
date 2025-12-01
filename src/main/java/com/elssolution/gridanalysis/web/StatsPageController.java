package com.elssolution.gridanalysis.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatsPageController {

    @GetMapping("/stats")
    public String statsPage() {
        return "stats.html"; // Spring will load it from /static/
    }
}
