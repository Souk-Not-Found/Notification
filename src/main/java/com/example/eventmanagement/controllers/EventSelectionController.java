package com.example.eventmanagement.controllers;

import com.example.eventmanagement.entities.EventSelection;
import com.example.eventmanagement.services.EventSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/event-selections")
@CrossOrigin(origins = "http://localhost:4200")
public class EventSelectionController {

    @Autowired
    private EventSelectionService eventSelectionService;

    /**
     * Create a new event selection
     */
    @PostMapping
    public ResponseEntity<EventSelectionService.EventSelectionResult> createEventSelection(
            @RequestBody CreateEventSelectionRequest request) {
        
        try {
            EventSelectionService.EventSelectionResult result = eventSelectionService.createEventSelection(
                    request.getUserId(),
                    request.getUserName(),
                    request.getEventId(),
                    request.getEventName(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing event selection
     */
    @PutMapping("/{selectionId}")
    public ResponseEntity<EventSelectionService.EventSelectionResult> updateEventSelection(
            @PathVariable Long selectionId,
            @RequestBody UpdateEventSelectionRequest request) {
        
        try {
            EventSelectionService.EventSelectionResult result = eventSelectionService.updateEventSelection(
                    selectionId,
                    request.getStartDate(),
                    request.getEndDate()
            );
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cancel an event selection
     */
    @DeleteMapping("/{selectionId}")
    public ResponseEntity<Void> cancelEventSelection(@PathVariable Long selectionId) {
        try {
            eventSelectionService.cancelEventSelection(selectionId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Confirm an event selection (admin action)
     */
    @PostMapping("/{selectionId}/confirm")
    public ResponseEntity<Void> confirmEventSelection(@PathVariable Long selectionId) {
        try {
            eventSelectionService.confirmEventSelection(selectionId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all selections for a specific event
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventSelection>> getSelectionsForEvent(@PathVariable Long eventId) {
        try {
            List<EventSelection> selections = eventSelectionService.getSelectionsForEvent(eventId);
            return ResponseEntity.ok(selections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all selections for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventSelection>> getSelectionsForUser(@PathVariable String userId) {
        try {
            List<EventSelection> selections = eventSelectionService.getSelectionsForUser(userId);
            return ResponseEntity.ok(selections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get overlapping selections for a specific event and time period
     */
    @GetMapping("/overlapping")
    public ResponseEntity<List<EventSelection>> getOverlappingSelections(
            @RequestParam Long eventId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<EventSelection> overlappingSelections = eventSelectionService
                    .getOverlappingSelections(eventId, start, end);
            
            return ResponseEntity.ok(overlappingSelections);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if a time period has conflicts for a specific event
     */
    @GetMapping("/check-conflicts")
    public ResponseEntity<ConflictCheckResponse> checkConflicts(
            @RequestParam Long eventId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long excludeSelectionId) {
        
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<EventSelection> overlappingSelections;
            
            if (excludeSelectionId != null) {
                // Use the repository method that excludes a specific selection
                overlappingSelections = eventSelectionService.getOverlappingSelections(eventId, start, end)
                        .stream()
                        .filter(selection -> !selection.getId().equals(excludeSelectionId))
                        .toList();
            } else {
                overlappingSelections = eventSelectionService.getOverlappingSelections(eventId, start, end);
            }
            
            ConflictCheckResponse response = new ConflictCheckResponse(
                    !overlappingSelections.isEmpty(),
                    overlappingSelections.size(),
                    overlappingSelections
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Request/Response DTOs

    public static class CreateEventSelectionRequest {
        private String userId;
        private String userName;
        private Long eventId;
        private String eventName;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }

        public String getEventName() { return eventName; }
        public void setEventName(String eventName) { this.eventName = eventName; }

        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }

    public static class UpdateEventSelectionRequest {
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        // Getters and Setters
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }

    public static class ConflictCheckResponse {
        private boolean hasConflicts;
        private int conflictCount;
        private List<EventSelection> conflictingSelections;

        public ConflictCheckResponse(boolean hasConflicts, int conflictCount, List<EventSelection> conflictingSelections) {
            this.hasConflicts = hasConflicts;
            this.conflictCount = conflictCount;
            this.conflictingSelections = conflictingSelections;
        }

        // Getters
        public boolean isHasConflicts() { return hasConflicts; }
        public int getConflictCount() { return conflictCount; }
        public List<EventSelection> getConflictingSelections() { return conflictingSelections; }
    }
} 