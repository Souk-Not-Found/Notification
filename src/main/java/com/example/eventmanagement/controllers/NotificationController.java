package com.example.eventmanagement.controllers;

import com.example.eventmanagement.entities.Notification;
import com.example.eventmanagement.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable String userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    @PostMapping("/")
    public void sendNotification(@RequestBody Notification notification) {
        notificationService.sendNotification(notification);
    }
}
