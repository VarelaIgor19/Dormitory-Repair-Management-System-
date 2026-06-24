package com.wut.dormrepair.dto;

public class DormitoryStatisticsDTO {

    private String campus;      // ✅ ADDED: 校区
    private String building;
    private Long pending;
    private Long inProgress;
    private Long completed;
    private Long total;

    // ✅ UPDATED: Constructor with campus parameter
    public DormitoryStatisticsDTO(String campus,
                                  String building,
                                  Long pending,
                                  Long inProgress,
                                  Long completed,
                                  Long total) {
        this.campus = campus;
        this.building = building;
        this.pending = pending;
        this.inProgress = inProgress;
        this.completed = completed;
        this.total = total;
    }

    // ✅ ADDED: Getter for campus
    public String getCampus() { return campus; }

    public String getBuilding() { return building; }
    public Long getPending() { return pending; }
    public Long getInProgress() { return inProgress; }
    public Long getCompleted() { return completed; }
    public Long getTotal() { return total; }
}