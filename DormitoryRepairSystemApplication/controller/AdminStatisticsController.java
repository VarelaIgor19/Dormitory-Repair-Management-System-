package com.wut.dormrepair.controller;

import com.wut.dormrepair.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {


    private final StatisticsService statisticsService;

    public AdminStatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/monthly")
    public Map<String,Object> monthlyStatistics(){
        return statisticsService.getMonthlyStatistics();
    }

    @GetMapping("/daily")
    public ResponseEntity<?> dailyReport(){
        return ResponseEntity.ok(statisticsService.getDailyReport());
    }


    @GetMapping("/dormitory")
    public ResponseEntity<?> dormitoryStatistics() {
        return ResponseEntity.ok(statisticsService.getDormitoryStatistics());
    }


}
