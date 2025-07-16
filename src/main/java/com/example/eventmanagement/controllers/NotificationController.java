package com.example.eventmanagement.controllers;

import com.example.eventmanagement.entities.Message;
import com.example.eventmanagement.entities.Notification;
import com.example.eventmanagement.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    
    @Autowired
    NotificationService notificationService;

    // WebSocket endpoints
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message getMessage(final Message message){
        message.setMessageContent(HtmlUtils.htmlEscape(message.getMessageContent()));
        System.out.println("!!! Message from BACKEND " + message.getMessageContent());
        notificationService.sendPublicNoti("Public Notification");
        return message;
    }

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public Message getPrivateMessage(final Message message, final Principal principal){
        message.setMessageContent(HtmlUtils.htmlEscape(principal.getName() +" %% "+message.getMessageContent()));
        System.out.println("!!! Private Message from BACKEND " + message.getMessageContent());
        notificationService.sendPrivateNoti(principal.getName(), "Private Notification");
        return message;
    }

    // REST endpoints for notification management
    
    // Get all notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    // Get notifications for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
    
    // Get public notifications
    @GetMapping("/public")
    public ResponseEntity<List<Notification>> getPublicNotifications() {
        List<Notification> notifications = notificationService.getPublicNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    // Get unread notifications for a user
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsForUser(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
    
    // Get notification by ID
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Mark notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
    
    // Delete old notifications
    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> deleteOldNotifications(@RequestParam(defaultValue = "30") int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        notificationService.deleteOldNotifications(cutoffDate);
        return ResponseEntity.ok().build();
    }
    
    // Send a test notification
    @PostMapping("/send-test")
    public ResponseEntity<Notification> sendTestNotification(@RequestBody String message) {
        Notification notification = notificationService.sendPublicNoti(message);
        return ResponseEntity.ok(notification);
    }
    
    // Send a test notification with event ID
    @PostMapping("/send-test-event")
    public ResponseEntity<Notification> sendTestEventNotification(@RequestBody TestEventNotificationRequest request) {
        String notificationMessage = String.format("New event created: %s (ID: %d)", request.eventName, request.eventId);
        Notification notification = notificationService.sendEventNotification(notificationMessage, Notification.NotificationType.EVENT_CREATED);
        return ResponseEntity.ok(notification);
    }
    
    // Request DTO for test event notification
    public static class TestEventNotificationRequest {
        private String eventName;
        private Long eventId;
        
        // Getters and Setters
        public String getEventName() { return eventName; }
        public void setEventName(String eventName) { this.eventName = eventName; }
        
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
    }
}
