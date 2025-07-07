package com.example.eventmanagement.controllers;

import com.example.eventmanagement.entities.Event;
import com.example.eventmanagement.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Create a new event
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequest request) {
        try {
            Event event = eventService.createEvent(
                    request.getName(),
                    request.getDescription(),
                    request.getLocation(),
                    request.getCapacity(),
                    request.getCreatedBy()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all active events
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAllActiveEvents() {
        try {
            List<Event> events = eventService.getAllActiveEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all events (including inactive) - Admin only
     */
    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get event by ID (only active events)
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Long eventId) {
        try {
            Optional<Event> event = eventService.getActiveEventById(eventId);
            if (event.isPresent()) {
                return ResponseEntity.ok(event.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get event by ID (including inactive) - Admin only
     */
    @GetMapping("/{eventId}/admin")
    public ResponseEntity<Event> getEventByIdAdmin(@PathVariable Long eventId) {
        try {
            Optional<Event> event = eventService.getEventById(eventId);
            if (event.isPresent()) {
                return ResponseEntity.ok(event.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing event
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long eventId,
            @RequestBody UpdateEventRequest request) {
        try {
            Event updatedEvent = eventService.updateEvent(
                    eventId,
                    request.getName(),
                    request.getDescription(),
                    request.getLocation(),
                    request.getCapacity()
            );
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deactivate an event (soft delete)
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deactivateEvent(@PathVariable Long eventId) {
        try {
            eventService.deactivateEvent(eventId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activate an event
     */
    @PostMapping("/{eventId}/activate")
    public ResponseEntity<Void> activateEvent(@PathVariable Long eventId) {
        try {
            eventService.activateEvent(eventId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search events by term
     */
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam(required = false) String q) {
        try {
            List<Event> events = eventService.searchEvents(q);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get events by location
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Event>> getEventsByLocation(@PathVariable String location) {
        try {
            List<Event> events = eventService.getEventsByLocation(location);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get events with available capacity
     */
    @GetMapping("/available")
    public ResponseEntity<List<Event>> getEventsWithAvailableCapacity() {
        try {
            List<Event> events = eventService.getEventsWithAvailableCapacity();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get fully booked events
     */
    @GetMapping("/booked")
    public ResponseEntity<List<Event>> getFullyBookedEvents() {
        try {
            List<Event> events = eventService.getFullyBookedEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get events created by a specific user
     */
    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<List<Event>> getEventsByCreator(@PathVariable String createdBy) {
        try {
            List<Event> events = eventService.getEventsByCreator(createdBy);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get event statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<EventService.EventStatistics> getEventStatistics() {
        try {
            EventService.EventStatistics stats = eventService.getEventStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Request DTOs

    public static class CreateEventRequest {
        private String name;
        private String description;
        private String location;
        private Integer capacity;
        private String createdBy;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }

    public static class UpdateEventRequest {
        private String name;
        private String description;
        private String location;
        private Integer capacity;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
    }
} 