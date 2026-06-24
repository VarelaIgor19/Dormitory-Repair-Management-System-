package com.wut.dormrepair.repository;

import com.wut.dormrepair.entity.StaffAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StaffAssignmentRepository extends JpaRepository<StaffAssignment, Long> {
    List<StaffAssignment> findByStaffId(Long staffId);
    List<StaffAssignment> findByStudentId(Long studentId);
    Optional<StaffAssignment> findByRepairOrderId(Long repairOrderId);
    List<StaffAssignment> findByStatus(String status);
}