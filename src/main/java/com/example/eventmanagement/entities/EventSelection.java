package com.example.eventmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_selections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSelection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    @Column(name = "event_name", nullable = false)
    private String eventName;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SelectionStatus status;
    
    public enum SelectionStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        CONFLICT
    }
    
    // Constructor for creating new selections
    public EventSelection(String userId, String userName, Long eventId, String eventName, 
                         LocalDateTime startDate, LocalDateTime endDate) {
        this.userId = userId;
        this.userName = userName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
        this.status = SelectionStatus.PENDING;
    }
    
    // Check if this selection overlaps with another
    public boolean overlapsWith(EventSelection other) {
        return this.eventId.equals(other.getEventId()) &&
               !this.endDate.isBefore(other.getStartDate()) &&
               !other.getEndDate().isBefore(this.startDate);
    }
    
    // Check if this selection overlaps with given time period
    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        return !this.endDate.isBefore(start) && !end.isBefore(this.startDate);
    }
} 