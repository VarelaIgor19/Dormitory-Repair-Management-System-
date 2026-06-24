package com.wut.dormrepair.dto;

public class DailyReportDTO {

    private String date;
    private long total;
    private long pending;
    private long inProgress;
    private long completed;

    public DailyReportDTO(String date, long total, long pending, long inProgress, long completed) {
        this.date = date;
        this.total = total;
        this.pending = pending;
        this.inProgress = inProgress;
        this.completed = completed;
    }

    public String getDate() { return date; }
    public long getTotal() { return total; }
    public long getPending() { return pending; }
    public long getInProgress() { return inProgress; }
    public long getCompleted() { return completed; }
}
