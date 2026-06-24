package com.wut.dormrepair.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_order")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime completedTime;

    // ADDED: Fields for campus, building, roomNumber
    private String campus;
    private String building;
    private String roomNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "orders"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @JsonIgnoreProperties({"orders"})
    private Dormitory dormitory;

    public RepairOrder() {
    }

    @PrePersist
    public void onCreate() {
        this.createTime = LocalDateTime.now();
        this.status = "待处理";
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    // ADDED: Public getters and setters for campus
    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    // ADDED: Public getters and setters for building
    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    // ADDED: Public getters and setters for roomNumber
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Dormitory getDormitory() {
        return dormitory;
    }

    public void setDormitory(Dormitory dormitory) {
        this.dormitory = dormitory;
    }
}