package com.example.eventmanagement.services;

import com.example.eventmanagement.entities.EventSelection;
import com.example.eventmanagement.entities.Message;
import com.example.eventmanagement.repositories.EventSelectionRepository;
import com.example.eventmanagement.dto.SelectionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventSelectionService {

    @Autowired
    private EventSelectionRepository eventSelectionRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    /**
     * Create a new event selection and handle notifications
     */
    public EventSelectionResult createEventSelection(String userId, String userName, 
                                                   Long eventId, String eventName,
                                                   LocalDateTime startDate, LocalDateTime endDate) {
        
        // Validate input
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        // Check for overlapping selections
        List<EventSelection> overlappingSelections = eventSelectionRepository
                .findOverlappingSelections(eventId, startDate, endDate);

        // Create the new selection
        EventSelection newSelection = new EventSelection(userId, userName, eventId, eventName, startDate, endDate);
        
        // Set status based on overlaps
        if (!overlappingSelections.isEmpty()) {
            newSelection.setStatus(EventSelection.SelectionStatus.CONFLICT);
        }

        // Save the selection
        EventSelection savedSelection = eventSelectionRepository.save(newSelection);

        // Handle notifications
        handleNotifications(savedSelection, overlappingSelections);

        return new EventSelectionResult(savedSelection, overlappingSelections);
    }

    /**
     * Update an existing event selection
     */
    public EventSelectionResult updateEventSelection(Long selectionId, LocalDateTime newStartDate, 
                                                   LocalDateTime newEndDate) {
        
        Optional<EventSelection> optionalSelection = eventSelectionRepository.findById(selectionId);
        if (optionalSelection.isEmpty()) {
            throw new IllegalArgumentException("Event selection not found with id: " + selectionId);
        }

        EventSelection existingSelection = optionalSelection.get();

        // Check for overlapping selections (excluding the current one)
        List<EventSelection> overlappingSelections = eventSelectionRepository
                .findOverlappingSelectionsExcluding(existingSelection.getEventId(), 
                                                   newStartDate, newEndDate, selectionId);

        // Update the selection
        existingSelection.setStartDate(newStartDate);
        existingSelection.setEndDate(newEndDate);
        
        // Update status based on overlaps
        if (!overlappingSelections.isEmpty()) {
            existingSelection.setStatus(EventSelection.SelectionStatus.CONFLICT);
        } else {
            existingSelection.setStatus(EventSelection.SelectionStatus.PENDING);
        }

        EventSelection updatedSelection = eventSelectionRepository.save(existingSelection);

        // Handle notifications
        handleNotifications(updatedSelection, overlappingSelections);

        return new EventSelectionResult(updatedSelection, overlappingSelections);
    }

    /**
     * Cancel an event selection
     */
    public void cancelEventSelection(Long selectionId) {
        Optional<EventSelection> optionalSelection = eventSelectionRepository.findById(selectionId);
        if (optionalSelection.isEmpty()) {
            throw new IllegalArgumentException("Event selection not found with id: " + selectionId);
        }

        EventSelection selection = optionalSelection.get();
        selection.setStatus(EventSelection.SelectionStatus.CANCELLED);
        eventSelectionRepository.save(selection);

        // Notify admin about cancellation
        notifyAdmin("Event selection cancelled: " + selection.getUserName() + 
                   " cancelled selection for event " + selection.getEventName());
    }

    /**
     * Confirm an event selection (admin action)
     */
    public void confirmEventSelection(Long selectionId) {
        Optional<EventSelection> optionalSelection = eventSelectionRepository.findById(selectionId);
        if (optionalSelection.isEmpty()) {
            throw new IllegalArgumentException("Event selection not found with id: " + selectionId);
        }

        EventSelection selection = optionalSelection.get();
        selection.setStatus(EventSelection.SelectionStatus.CONFIRMED);
        eventSelectionRepository.save(selection);

        // Notify user about confirmation
        notifyUser(selection.getUserId(), "Your event selection for " + selection.getEventName() + 
                 " has been confirmed by admin.");
    }

    /**
     * Get all selections for a specific event
     */
    public List<EventSelection> getSelectionsForEvent(Long eventId) {
        return eventSelectionRepository.findByEventId(eventId);
    }

    /**
     * Get all selections for a specific user
     */
    public List<EventSelection> getSelectionsForUser(String userId) {
        return eventSelectionRepository.findByUserId(userId);
    }

    /**
     * Get overlapping selections for a specific event and time period
     */
    public List<EventSelection> getOverlappingSelections(Long eventId, LocalDateTime startDate, LocalDateTime endDate) {
        return eventSelectionRepository.findOverlappingSelections(eventId, startDate, endDate);
    }

    /**
     * Get all event selections (for admin)
     */
    public List<EventSelection> getAllEventSelections() {
        return eventSelectionRepository.findAll();
    }

    /**
     * Get all selections with a specific status
     */
    public List<EventSelection> getSelectionsByStatus(EventSelection.SelectionStatus status) {
        return eventSelectionRepository.findByStatus(status);
    }

    /**
     * Get selection statistics
     */
    public SelectionStatistics getSelectionStatistics() {
        long totalSelections = eventSelectionRepository.count();
        long pendingSelections = eventSelectionRepository.countByStatus(EventSelection.SelectionStatus.PENDING);
        long confirmedSelections = eventSelectionRepository.countByStatus(EventSelection.SelectionStatus.CONFIRMED);
        long cancelledSelections = eventSelectionRepository.countByStatus(EventSelection.SelectionStatus.CANCELLED);
        long conflictingSelections = eventSelectionRepository.countByStatus(EventSelection.SelectionStatus.CONFLICT);
        
        return new SelectionStatistics(
                totalSelections, pendingSelections, confirmedSelections, 
                cancelledSelections, conflictingSelections);
    }

    /**
     * Handle notifications for new/updated selections
     */
    private void handleNotifications(EventSelection newSelection, List<EventSelection> overlappingSelections) {
        
        // Notify admin about new selection
        String adminMessage = String.format("New event selection: %s selected event '%s' for period %s to %s", 
                                          newSelection.getUserName(), 
                                          newSelection.getEventName(),
                                          newSelection.getStartDate().toString(),
                                          newSelection.getEndDate().toString());
        
        if (!overlappingSelections.isEmpty()) {
            adminMessage += " (CONFLICT DETECTED with " + overlappingSelections.size() + " existing selections)";
        }
        
        notifyAdmin(adminMessage);

        // Notify users with overlapping selections
        for (EventSelection overlapping : overlappingSelections) {
            String conflictMessage = String.format("CONFLICT: Another user has selected event '%s' for an overlapping period (%s to %s). " +
                                                 "Please check your selection.", 
                                                 newSelection.getEventName(),
                                                 newSelection.getStartDate().toString(),
                                                 newSelection.getEndDate().toString());
            
            notifyUser(overlapping.getUserId(), conflictMessage);
        }

        // Notify the new user if there are conflicts
        if (!overlappingSelections.isEmpty()) {
            String userConflictMessage = String.format("Your selection for event '%s' conflicts with %d existing selections. " +
                                                     "Please contact admin for resolution.", 
                                                     newSelection.getEventName(), 
                                                     overlappingSelections.size());
            notifyUser(newSelection.getUserId(), userConflictMessage);
        }
    }

    /**
     * Send notification to a specific user
     */
    private void notifyUser(String userId, String message) {
        Message notification = new Message(message);
        messagingTemplate.convertAndSendToUser(userId, "/topic/private-notifications", notification);
        System.out.println("Notification sent to user " + userId + ": " + message);
    }

    /**
     * Send notification to admin
     */
    private void notifyAdmin(String message) {
        Message notification = new Message(message);
        messagingTemplate.convertAndSend("/topic/admin-notifications", notification);
        System.out.println("Admin notification: " + message);
    }

    /**
     * Result class for event selection operations
     */
    public static class EventSelectionResult {
        private final EventSelection selection;
        private final List<EventSelection> overlappingSelections;
        private final boolean hasConflicts;

        public EventSelectionResult(EventSelection selection, List<EventSelection> overlappingSelections) {
            this.selection = selection;
            this.overlappingSelections = overlappingSelections;
            this.hasConflicts = !overlappingSelections.isEmpty();
        }

        public EventSelection getSelection() {
            return selection;
        }

        public List<EventSelection> getOverlappingSelections() {
            return overlappingSelections;
        }

        public boolean hasConflicts() {
            return hasConflicts;
        }
    }
} 