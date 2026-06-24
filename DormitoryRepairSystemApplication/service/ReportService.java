package com.wut.dormrepair.service;

import com.wut.dormrepair.dto.RepairStatusReportDTO;
import com.wut.dormrepair.repository.RepairOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final RepairOrderRepository repairOrderRepository;

    public ReportService(RepairOrderRepository repairOrderRepository) {
        this.repairOrderRepository = repairOrderRepository;
    }

    public RepairStatusReportDTO getStatusReport() {

        long pending = repairOrderRepository.countPending();
        long inProgress = repairOrderRepository.countInProgress();
        long completed = repairOrderRepository.countCompleted();

        return new RepairStatusReportDTO(pending, inProgress, completed);
    }
}
