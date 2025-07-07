package com.example.eventmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "location", length = 255)
    private String location;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    // Constructor for creating new events
    public Event(String name, String description, String location, Integer capacity, String createdBy) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.capacity = capacity;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Method to update event details
    public void updateEvent(String name, String description, String location, Integer capacity) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.capacity = capacity;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Method to deactivate event
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Method to activate event
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 