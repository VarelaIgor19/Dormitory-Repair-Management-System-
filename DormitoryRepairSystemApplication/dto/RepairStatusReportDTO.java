package com.wut.dormrepair.dto;

public class RepairStatusReportDTO {

    private long pending;
    private long inProgress;
    private long completed;

    public RepairStatusReportDTO(long pending, long inProgress, long completed) {
        this.pending = pending;
        this.inProgress = inProgress;
        this.completed = completed;
    }

    public long getPending() { return pending; }
    public long getInProgress() { return inProgress; }
    public long getCompleted() { return completed; }
}
