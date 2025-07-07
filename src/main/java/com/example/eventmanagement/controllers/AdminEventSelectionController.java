package com.example.eventmanagement.controllers;

import com.example.eventmanagement.entities.EventSelection;
import com.example.eventmanagement.services.EventSelectionService;
import com.example.eventmanagement.dto.SelectionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/event-selections")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminEventSelectionController {

    @Autowired
    private EventSelectionService eventSelectionService;

    /**
     * Get all event selections (admin view)
     */
    @GetMapping
    public ResponseEntity<List<EventSelection>> getAllEventSelections() {
        try {
            // For now, we'll return all selections. In a real app, you might want pagination
            // and filtering options
            List<EventSelection> allSelections = eventSelectionService.getAllEventSelections();
            return ResponseEntity.ok(allSelections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all selections with a specific status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventSelection>> getSelectionsByStatus(
            @PathVariable EventSelection.SelectionStatus status) {
        try {
            List<EventSelection> selections = eventSelectionService.getSelectionsByStatus(status);
            return ResponseEntity.ok(selections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all selections with conflicts
     */
    @GetMapping("/conflicts")
    public ResponseEntity<List<EventSelection>> getConflictingSelections() {
        try {
            List<EventSelection> conflictingSelections = eventSelectionService.getSelectionsByStatus(
                    EventSelection.SelectionStatus.CONFLICT);
            return ResponseEntity.ok(conflictingSelections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all pending selections
     */
    @GetMapping("/pending")
    public ResponseEntity<List<EventSelection>> getPendingSelections() {
        try {
            List<EventSelection> pendingSelections = eventSelectionService.getSelectionsByStatus(
                    EventSelection.SelectionStatus.PENDING);
            return ResponseEntity.ok(pendingSelections);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Confirm multiple event selections at once
     */
    @PostMapping("/confirm-multiple")
    public ResponseEntity<BulkOperationResponse> confirmMultipleSelections(
            @RequestBody BulkSelectionRequest request) {
        
        try {
            BulkOperationResponse response = new BulkOperationResponse();
            
            for (Long selectionId : request.getSelectionIds()) {
                try {
                    eventSelectionService.confirmEventSelection(selectionId);
                    response.getSuccessful().add(selectionId);
                } catch (Exception e) {
                    response.getFailed().add(selectionId);
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cancel multiple event selections at once
     */
    @PostMapping("/cancel-multiple")
    public ResponseEntity<BulkOperationResponse> cancelMultipleSelections(
            @RequestBody BulkSelectionRequest request) {
        
        try {
            BulkOperationResponse response = new BulkOperationResponse();
            
            for (Long selectionId : request.getSelectionIds()) {
                try {
                    eventSelectionService.cancelEventSelection(selectionId);
                    response.getSuccessful().add(selectionId);
                } catch (Exception e) {
                    response.getFailed().add(selectionId);
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Resolve a conflict by confirming one selection and cancelling others
     */
    @PostMapping("/resolve-conflict")
    public ResponseEntity<Void> resolveConflict(@RequestBody ConflictResolutionRequest request) {
        try {
            // Confirm the selected choice
            eventSelectionService.confirmEventSelection(request.getSelectedSelectionId());
            
            // Cancel the conflicting selections
            for (Long conflictingId : request.getConflictingSelectionIds()) {
                eventSelectionService.cancelEventSelection(conflictingId);
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get statistics about event selections
     */
    @GetMapping("/statistics")
    public ResponseEntity<SelectionStatistics> getSelectionStatistics() {
        try {
            SelectionStatistics stats = eventSelectionService.getSelectionStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Request/Response DTOs

    public static class BulkSelectionRequest {
        private List<Long> selectionIds;

        public List<Long> getSelectionIds() { return selectionIds; }
        public void setSelectionIds(List<Long> selectionIds) { this.selectionIds = selectionIds; }
    }

    public static class BulkOperationResponse {
        private List<Long> successful = new java.util.ArrayList<>();
        private List<Long> failed = new java.util.ArrayList<>();

        public List<Long> getSuccessful() { return successful; }
        public void setSuccessful(List<Long> successful) { this.successful = successful; }

        public List<Long> getFailed() { return failed; }
        public void setFailed(List<Long> failed) { this.failed = failed; }
    }

    public static class ConflictResolutionRequest {
        private Long selectedSelectionId;
        private List<Long> conflictingSelectionIds;

        public Long getSelectedSelectionId() { return selectedSelectionId; }
        public void setSelectedSelectionId(Long selectedSelectionId) { this.selectedSelectionId = selectedSelectionId; }

        public List<Long> getConflictingSelectionIds() { return conflictingSelectionIds; }
        public void setConflictingSelectionIds(List<Long> conflictingSelectionIds) { this.conflictingSelectionIds = conflictingSelectionIds; }
    }


} 