package com.elssolution.gridanalysis.web;

import com.elssolution.gridanalysis.service.DailyStatsService;
import com.elssolution.gridanalysis.domain.DailyStats;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DailyStatsController {

    private final DailyStatsService statsService;

    public DailyStatsController(DailyStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/api/stats/today")
    public DailyStats getTodayStats() {
        return statsService.getToday();
    }

    @GetMapping("/api/stats")
    public DailyStats getStatsByDate(@RequestParam String date) {
        return statsService.loadByDate(date);
    }
}

