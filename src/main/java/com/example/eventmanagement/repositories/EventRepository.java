package com.example.eventmanagement.repositories;

import com.example.eventmanagement.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Find all active events
    List<Event> findByIsActiveTrue();
    
    // Find all inactive events
    List<Event> findByIsActiveFalse();
    
    // Find events by name (case-insensitive)
    List<Event> findByNameContainingIgnoreCase(String name);
    
    // Find events by location
    List<Event> findByLocationContainingIgnoreCase(String location);
    
    // Find events created by a specific user
    List<Event> findByCreatedBy(String createdBy);
    
    // Find active events by location
    List<Event> findByLocationContainingIgnoreCaseAndIsActiveTrue(String location);
    
    // Find events with capacity greater than or equal to a value
    List<Event> findByCapacityGreaterThanEqualAndIsActiveTrue(Integer capacity);
    
    // Find events created after a specific date
    List<Event> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    // Find events updated after a specific date
    List<Event> findByUpdatedAtAfter(java.time.LocalDateTime date);
    
    // Check if event exists and is active
    boolean existsByIdAndIsActiveTrue(Long id);
    
    // Find event by ID only if it's active
    Optional<Event> findByIdAndIsActiveTrue(Long id);
    
    // Count active events
    long countByIsActiveTrue();
    
    // Count events by location
    long countByLocation(String location);
    
    // Find events with similar names (for search functionality)
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND " +
           "(LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Event> searchActiveEvents(@Param("searchTerm") String searchTerm);
    
    // Find events with available capacity
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND " +
           "(e.capacity IS NULL OR e.capacity > " +
           "(SELECT COUNT(es) FROM EventSelection es WHERE es.eventId = e.id AND es.status != 'CANCELLED'))")
    List<Event> findEventsWithAvailableCapacity();
    
    // Find events that are fully booked
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.capacity IS NOT NULL AND " +
           "e.capacity <= (SELECT COUNT(es) FROM EventSelection es WHERE es.eventId = e.id AND es.status != 'CANCELLED')")
    List<Event> findFullyBookedEvents();
} 