package com.example.eventmanagement.repositories;

import com.example.eventmanagement.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find all notifications for a specific user
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    // Find all public notifications
    List<Notification> findByUserIdIsNullOrderByCreatedAtDesc();
    
    // Find notifications by type
    List<Notification> findByTypeOrderByCreatedAtDesc(Notification.NotificationType type);
    
    // Find unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);
    
    // Find all unread notifications
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();
    
    // Find notifications created after a specific date
    List<Notification> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
    
    // Count unread notifications for a user
    long countByUserIdAndIsReadFalse(String userId);
    
    // Count all unread notifications
    long countByIsReadFalse();
    
    // Find notifications by user and type
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, Notification.NotificationType type);
    
    // Delete notifications older than a specific date
    @Query("DELETE FROM Notification n WHERE n.createdAt < :date")
    void deleteNotificationsOlderThan(@Param("date") LocalDateTime date);
} 