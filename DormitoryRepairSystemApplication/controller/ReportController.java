package com.wut.dormrepair.controller;

import com.wut.dormrepair.dto.RepairStatusReportDTO;
import com.wut.dormrepair.service.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/status")
    public RepairStatusReportDTO getStatusReport() {
        return reportService.getStatusReport();
    }
}
