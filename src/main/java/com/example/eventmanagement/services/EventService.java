package com.example.eventmanagement.services;

import com.example.eventmanagement.entities.Event;
import com.example.eventmanagement.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    /**
     * Create a new event
     */
    public Event createEvent(String name, String description, String location, Integer capacity, String createdBy) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        
        if (capacity != null && capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive if specified");
        }

        Event event = new Event(name.trim(), description, location, capacity, createdBy);
        return eventRepository.save(event);
    }

    /**
     * Get all active events
     */
    public List<Event> getAllActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }

    /**
     * Get all events (including inactive)
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Get event by ID (only if active)
     */
    public Optional<Event> getActiveEventById(Long id) {
        return eventRepository.findByIdAndIsActiveTrue(id);
    }

    /**
     * Get event by ID (including inactive)
     */
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    /**
     * Update an existing event
     */
    public Event updateEvent(Long eventId, String name, String description, String location, Integer capacity) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }

        Event event = optionalEvent.get();
        
        // Validate input
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        
        if (capacity != null && capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive if specified");
        }

        // Update only non-null fields
        if (name != null) {
            event.setName(name.trim());
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (location != null) {
            event.setLocation(location);
        }
        if (capacity != null) {
            event.setCapacity(capacity);
        }

        return eventRepository.save(event);
    }

    /**
     * Deactivate an event
     */
    public void deactivateEvent(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }

        Event event = optionalEvent.get();
        event.deactivate();
        eventRepository.save(event);
    }

    /**
     * Activate an event
     */
    public void activateEvent(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }

        Event event = optionalEvent.get();
        event.activate();
        eventRepository.save(event);
    }

    /**
     * Delete an event (soft delete by deactivating)
     */
    public void deleteEvent(Long eventId) {
        deactivateEvent(eventId);
    }

    /**
     * Search events by term (name, description, or location)
     */
    public List<Event> searchEvents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllActiveEvents();
        }
        return eventRepository.searchActiveEvents(searchTerm.trim());
    }

    /**
     * Get events by location
     */
    public List<Event> getEventsByLocation(String location) {
        return eventRepository.findByLocationContainingIgnoreCaseAndIsActiveTrue(location);
    }

    /**
     * Get events with available capacity
     */
    public List<Event> getEventsWithAvailableCapacity() {
        return eventRepository.findEventsWithAvailableCapacity();
    }

    /**
     * Get fully booked events
     */
    public List<Event> getFullyBookedEvents() {
        return eventRepository.findFullyBookedEvents();
    }

    /**
     * Get events created by a specific user
     */
    public List<Event> getEventsByCreator(String createdBy) {
        return eventRepository.findByCreatedBy(createdBy);
    }

    /**
     * Get events created after a specific date
     */
    public List<Event> getEventsCreatedAfter(LocalDateTime date) {
        return eventRepository.findByCreatedAtAfter(date);
    }

    /**
     * Check if event exists and is active
     */
    public boolean isEventActive(Long eventId) {
        return eventRepository.existsByIdAndIsActiveTrue(eventId);
    }

    /**
     * Get event statistics
     */
    public EventStatistics getEventStatistics() {
        long totalEvents = eventRepository.count();
        long activeEvents = eventRepository.countByIsActiveTrue();
        long inactiveEvents = totalEvents - activeEvents;
        
        List<Event> eventsWithCapacity = eventRepository.findEventsWithAvailableCapacity();
        List<Event> fullyBookedEvents = eventRepository.findFullyBookedEvents();
        
        return new EventStatistics(
                totalEvents,
                activeEvents,
                inactiveEvents,
                eventsWithCapacity.size(),
                fullyBookedEvents.size()
        );
    }

    /**
     * Validate event for selection
     */
    public void validateEventForSelection(Long eventId) {
        if (!isEventActive(eventId)) {
            throw new IllegalArgumentException("Event with id " + eventId + " does not exist or is not active");
        }
    }

    /**
     * Get event name by ID (for validation)
     */
    public String getEventNameById(Long eventId) {
        Optional<Event> event = getActiveEventById(eventId);
        if (event.isEmpty()) {
            throw new IllegalArgumentException("Event with id " + eventId + " does not exist or is not active");
        }
        return event.get().getName();
    }

    /**
     * Statistics class for events
     */
    public static class EventStatistics {
        private final long totalEvents;
        private final long activeEvents;
        private final long inactiveEvents;
        private final long eventsWithAvailableCapacity;
        private final long fullyBookedEvents;

        public EventStatistics(long totalEvents, long activeEvents, long inactiveEvents, 
                             long eventsWithAvailableCapacity, long fullyBookedEvents) {
            this.totalEvents = totalEvents;
            this.activeEvents = activeEvents;
            this.inactiveEvents = inactiveEvents;
            this.eventsWithAvailableCapacity = eventsWithAvailableCapacity;
            this.fullyBookedEvents = fullyBookedEvents;
        }

        // Getters
        public long getTotalEvents() { return totalEvents; }
        public long getActiveEvents() { return activeEvents; }
        public long getInactiveEvents() { return inactiveEvents; }
        public long getEventsWithAvailableCapacity() { return eventsWithAvailableCapacity; }
        public long getFullyBookedEvents() { return fullyBookedEvents; }
    }
} 