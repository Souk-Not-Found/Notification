package com.example.eventmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead;
    
    @Column(name = "user_id")
    private String userId; // null for public notifications
    
    public enum NotificationType {
        PUBLIC,
        PRIVATE,
        EVENT_CREATED,
        EVENT_UPDATED,
        EVENT_CANCELLED
    }
    
    // Constructor for creating new notifications
    public Notification(String message, NotificationType type, String userId) {
        this.message = message;
        this.type = type;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    // Constructor for public notifications
    public Notification(String message, NotificationType type) {
        this(message, type, null);
    }
    
    // Method to mark as read
    public void markAsRead() {
        this.isRead = true;
    }
} 