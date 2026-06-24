package com.wut.dormrepair.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dormitory")
public class Dormitory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String building;

    private String roomNumber;

    private String campus;  // ✅ ADDED: 校区 field

    public Dormitory() {
    }

    // ✅ ADDED: Constructor with campus
    public Dormitory(String building, String roomNumber, String campus) {
        this.building = building;
        this.roomNumber = roomNumber;
        this.campus = campus;
    }

    public Long getId() {
        return id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    // ✅ ADDED: Getter and setter for campus
    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getName() {
        return building + "-" + roomNumber;
    }
}