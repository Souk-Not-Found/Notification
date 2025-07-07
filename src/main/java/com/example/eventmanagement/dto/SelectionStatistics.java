package com.example.eventmanagement.dto;

public class SelectionStatistics {
    private long totalSelections;
    private long pendingSelections;
    private long confirmedSelections;
    private long cancelledSelections;
    private long conflictingSelections;

    public SelectionStatistics(long totalSelections, long pendingSelections, long confirmedSelections, 
                             long cancelledSelections, long conflictingSelections) {
        this.totalSelections = totalSelections;
        this.pendingSelections = pendingSelections;
        this.confirmedSelections = confirmedSelections;
        this.cancelledSelections = cancelledSelections;
        this.conflictingSelections = conflictingSelections;
    }

    // Default constructor for JSON serialization
    public SelectionStatistics() {
    }

    // Getters and Setters
    public long getTotalSelections() { return totalSelections; }
    public void setTotalSelections(long totalSelections) { this.totalSelections = totalSelections; }

    public long getPendingSelections() { return pendingSelections; }
    public void setPendingSelections(long pendingSelections) { this.pendingSelections = pendingSelections; }

    public long getConfirmedSelections() { return confirmedSelections; }
    public void setConfirmedSelections(long confirmedSelections) { this.confirmedSelections = confirmedSelections; }

    public long getCancelledSelections() { return cancelledSelections; }
    public void setCancelledSelections(long cancelledSelections) { this.cancelledSelections = cancelledSelections; }

    public long getConflictingSelections() { return conflictingSelections; }
    public void setConflictingSelections(long conflictingSelections) { this.conflictingSelections = conflictingSelections; }
} 