package com.example.eventmanagement.entities;

import java.sql.Time;
import java.util.Date;

public class Notification {
    private Long id;
    private String recipientId; // user or admin ID
    private String message;
    private NotificationType type;
    private Time timestamp;
    private boolean read;
}
