package com.wut.dormrepair.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_assignment")
public class StaffAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repair_order_id")
    @JsonIgnoreProperties({"user", "dormitory"})
    private RepairOrder repairOrder;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    @JsonIgnoreProperties({"password"})
    private User staff;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties({"password"})
    private User student;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime assignedTime;

    @ManyToOne
    @JoinColumn(name = "assigned_by_admin_id")
    @JsonIgnoreProperties({"password"})
    private User assignedBy;

    @PrePersist
    public void onCreate() {
        this.assignedTime = LocalDateTime.now();
        this.status = "ASSIGNED";
    }

    public StaffAssignment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RepairOrder getRepairOrder() {
        return repairOrder;
    }

    public void setRepairOrder(RepairOrder repairOrder) {
        this.repairOrder = repairOrder;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(LocalDateTime assignedTime) {
        this.assignedTime = assignedTime;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }
}