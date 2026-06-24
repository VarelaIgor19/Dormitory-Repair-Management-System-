package com.wut.dormrepair.repository;

import com.wut.dormrepair.dto.DormitoryStatisticsDTO;
import com.wut.dormrepair.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;


public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

    // find repair orders by user id
    List<RepairOrder> findByUserId(Long userId);

    //find repair orders by Time
    List<RepairOrder> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    // find repair orders by status
    List<RepairOrder> findByStatus(String status);

    @Query("SELECT COUNT(r) FROM RepairOrder r WHERE r.status = '待处理'")
    long countPending();

    @Query("SELECT COUNT(r) FROM RepairOrder r WHERE r.status = '处理中'")
    long countInProgress();

    @Query("SELECT COUNT(r) FROM RepairOrder r WHERE r.status = '已完成'")
    long countCompleted();

    // 每日报表
    @Query(value = """
            SELECT 
                DATE(r.create_time) as date,
                COUNT(*) as total,
                SUM(CASE WHEN r.status = 'PENDING' THEN 1 ELSE 0 END) as pending,
                SUM(CASE WHEN r.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress,
                SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed
            FROM repair_order r
            GROUP BY DATE(r.create_time)
            ORDER BY DATE(r.create_time)
            """, nativeQuery = true)
    List<Object[]> getDailyReport();

    //✅ UPDATED: 宿舍楼报修统计 - now includes campus (校区)
    @Query("""
SELECT new com.wut.dormrepair.dto.DormitoryStatisticsDTO(
    d.campus,           
    d.building,
    SUM(CASE WHEN r.status = '待处理' THEN 1 ELSE 0 END),
    SUM(CASE WHEN r.status = '处理中' THEN 1 ELSE 0 END),
    SUM(CASE WHEN r.status = '已完成' THEN 1 ELSE 0 END),
    COUNT(r)
)
FROM RepairOrder r
JOIN r.dormitory d
GROUP BY d.campus, d.building    
ORDER BY d.campus, d.building   
""")
    List<DormitoryStatisticsDTO> getDormitoryStatistics();

}