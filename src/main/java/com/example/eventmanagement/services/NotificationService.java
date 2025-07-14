package com.example.eventmanagement.services;

import com.example.eventmanagement.entities.Message;
import com.example.eventmanagement.entities.Notification;
import com.example.eventmanagement.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    @Autowired
    private SimpMessagingTemplate template;
    
    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendPublicNoti(String content) {
        // Create and save notification to database
        Notification notification = new Notification(content, Notification.NotificationType.PUBLIC);
        notification = notificationRepository.save(notification);
        
        // Send via WebSocket
        Message message = new Message(content);
        template.convertAndSend("/topic/public-noti", message);
        
        System.out.println("Public notification saved with ID: " + notification.getId());
    }

    public void sendPrivateNoti(String id, String content) {
        // Create and save notification to database
        Notification notification = new Notification(content, Notification.NotificationType.PRIVATE, id);
        notification = notificationRepository.save(notification);
        
        // Send via WebSocket
        Message message = new Message(content);
        template.convertAndSendToUser(id, "/topic/private-noti", message);
        
        System.out.println("Private notification saved with ID: " + notification.getId());
    }
    
    // Send event-specific notification
    public void sendEventNotification(String content, Notification.NotificationType type) {
        Notification notification = new Notification(content, type);
        notification = notificationRepository.save(notification);
        
        // Send via WebSocket
        Message message = new Message(content);
        template.convertAndSend("/topic/public-noti", message);
        
        System.out.println("Event notification saved with ID: " + notification.getId());
    }
    
    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    // Get notifications for a specific user
    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // Get public notifications
    public List<Notification> getPublicNotifications() {
        return notificationRepository.findByUserIdIsNullOrderByCreatedAtDesc();
    }
    
    // Get unread notifications for a user
    public List<Notification> getUnreadNotificationsForUser(String userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    // Mark notification as read
    public void markAsRead(Long notificationId) {
        Optional<Notification> optional = notificationRepository.findById(notificationId);
        if (optional.isPresent()) {
            Notification notification = optional.get();
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }
    
    // Delete notification
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    // Delete notifications older than specified date
    public void deleteOldNotifications(LocalDateTime date) {
        notificationRepository.deleteNotificationsOlderThan(date);
    }
    
    // Get notification by ID
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
}
