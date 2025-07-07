package com.example.eventmanagement.repositories;

import com.example.eventmanagement.entities.EventSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventSelectionRepository extends JpaRepository<EventSelection, Long> {
    
    // Find all selections for a specific event
    List<EventSelection> findByEventId(Long eventId);
    
    // Find all selections for a specific user
    List<EventSelection> findByUserId(String userId);
    
    // Find all selections for a specific event and user
    List<EventSelection> findByEventIdAndUserId(Long eventId, String userId);
    
    // Find all selections with a specific status
    List<EventSelection> findByStatus(EventSelection.SelectionStatus status);
    
    // Find all selections for a specific event with a specific status
    List<EventSelection> findByEventIdAndStatus(Long eventId, EventSelection.SelectionStatus status);
    
    // Find overlapping selections for a specific event and time period
    @Query("SELECT es FROM EventSelection es WHERE es.eventId = :eventId " +
           "AND es.status != 'CANCELLED' " +
           "AND ((es.startDate <= :endDate AND es.endDate >= :startDate) " +
           "OR (es.startDate >= :startDate AND es.startDate <= :endDate) " +
           "OR (es.endDate >= :startDate AND es.endDate <= :endDate))")
    List<EventSelection> findOverlappingSelections(
            @Param("eventId") Long eventId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    // Find overlapping selections excluding a specific selection (for updates)
    @Query("SELECT es FROM EventSelection es WHERE es.eventId = :eventId " +
           "AND es.id != :excludeId " +
           "AND es.status != 'CANCELLED' " +
           "AND ((es.startDate <= :endDate AND es.endDate >= :startDate) " +
           "OR (es.startDate >= :startDate AND es.startDate <= :endDate) " +
           "OR (es.endDate >= :startDate AND es.endDate <= :endDate))")
    List<EventSelection> findOverlappingSelectionsExcluding(
            @Param("eventId") Long eventId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludeId") Long excludeId
    );
    
    // Find all selections within a date range
    @Query("SELECT es FROM EventSelection es WHERE " +
           "((es.startDate >= :startDate AND es.startDate <= :endDate) " +
           "OR (es.endDate >= :startDate AND es.endDate <= :endDate) " +
           "OR (es.startDate <= :startDate AND es.endDate >= :endDate))")
    List<EventSelection> findSelectionsInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    // Find all selections for a specific event in a date range
    @Query("SELECT es FROM EventSelection es WHERE es.eventId = :eventId " +
           "AND ((es.startDate >= :startDate AND es.startDate <= :endDate) " +
           "OR (es.endDate >= :startDate AND es.endDate <= :endDate) " +
           "OR (es.startDate <= :startDate AND es.endDate >= :endDate))")
    List<EventSelection> findSelectionsByEventInDateRange(
            @Param("eventId") Long eventId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    // Count selections for a specific event
    long countByEventId(Long eventId);
    
    // Count active selections for a specific event (not cancelled)
    @Query("SELECT COUNT(es) FROM EventSelection es WHERE es.eventId = :eventId AND es.status != 'CANCELLED'")
    long countActiveSelectionsByEventId(@Param("eventId") Long eventId);
    
    // Find selections created after a specific date
    List<EventSelection> findByCreatedAtAfter(LocalDateTime date);
    
    // Find selections by user and status
    List<EventSelection> findByUserIdAndStatus(String userId, EventSelection.SelectionStatus status);
    
    // Count selections by status
    long countByStatus(EventSelection.SelectionStatus status);
} 