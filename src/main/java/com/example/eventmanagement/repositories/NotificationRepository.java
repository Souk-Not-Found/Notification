package com.example.eventmanagement.repositories;

import com.example.eventmanagement.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

}
