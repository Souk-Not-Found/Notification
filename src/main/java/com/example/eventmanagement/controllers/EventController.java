package com.example.eventmanagement.controllers;

import com.example.eventmanagement.entities.Event;
import com.example.eventmanagement.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import com.example.eventmanagement.entities.Notification;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    @Autowired
    private NotificationService notificationService;

    // For demo: In-memory event storage (replace with repository in real app)
    // private final List<Event> events = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // Set creation date and mark as active
        event.setCreatedAt(LocalDateTime.now());
        event.setIsActive(true);
        // In a real app, save to DB here
        // events.add(event);

        // Send notification with event-specific type
        notificationService.sendEventNotification("A new event has been created: " + event.getName(), 
                                                 Notification.NotificationType.EVENT_CREATED);

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }
} 