package com.wut.dormrepair.service;

import com.wut.dormrepair.dto.DormitoryStatisticsDTO;
import com.wut.dormrepair.entity.RepairOrder;
import com.wut.dormrepair.repository.RepairOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class StatisticsService {

    private final RepairOrderRepository repairOrderRepository;

    public StatisticsService(RepairOrderRepository repairOrderRepository) {
        this.repairOrderRepository = repairOrderRepository;
    }

    //每月报修统计

    public Map<String, Object> getMonthlyStatistics() {

        //current month range
        YearMonth month = YearMonth.now();
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);

        List<RepairOrder> monthlyOrders =
                repairOrderRepository.findByCreateTimeBetween(start, end);

        int total = monthlyOrders.size();
        int completed = 0;
        int pending = 0;
        int inProgress = 0;

        Map<String, Integer> dormCounter = new HashMap<>();

        for (RepairOrder order : monthlyOrders) {

            //correct status classification
            if ("COMPLETED".equalsIgnoreCase(order.getStatus())) {
                completed++;
            }
            else if ("IN_PROGRESS".equalsIgnoreCase(order.getStatus())) {
                inProgress++;
            }
            else {
                pending++;
            }

            // count dormitory occurrences 宿舍事件的计数
            String dorm = order.getDormitory().getName();
            dormCounter.put(dorm, dormCounter.getOrDefault(dorm, 0) + 1);
        }

        // most problematic dormitory
        String worstDorm = dormCounter.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        Map<String, Object> result = new HashMap<>();
        result.put("订单总数", total);
        result.put("已完成", completed);
        result.put("处理中", inProgress);
        result.put("待处理", pending);
        result.put("问题最多宿舍楼", worstDorm);

        return result;
    }


    //宿舍楼报修统计（核心功能）
    public List<DormitoryStatisticsDTO> getDormitoryStatistics() {
        return repairOrderRepository.getDormitoryStatistics();
    }

    public List<com.wut.dormrepair.dto.DailyReportDTO> getDailyReport(){

        List<Object[]> results = repairOrderRepository.getDailyReport();
        List<com.wut.dormrepair.dto.DailyReportDTO> report = new ArrayList<>();

        for(Object[] row : results){

            String date = row[0].toString();
            long total = ((Number)row[1]).longValue();
            long pending = ((Number)row[2]).longValue();
            long inProgress = ((Number)row[3]).longValue();
            long completed = ((Number)row[4]).longValue();

            report.add(new com.wut.dormrepair.dto.DailyReportDTO(
                    date,total,pending,inProgress,completed
            ));
        }

        return report;
    }

}
