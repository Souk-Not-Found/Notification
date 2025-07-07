package com.example.eventmanagement.services;

import com.example.eventmanagement.entities.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private SimpMessagingTemplate template;

    public NotificationService ( SimpMessagingTemplate template){
        this.template = template;
    }

    public void sendPublicNoti(){

       /* Message message  = new Message("Public Notification");
        template.convertAndSend("/topic/public-noti", message);*/
    }

    public void sendPrivateNoti(final String id){
       /* Message message  = new Message("Private Notification");
        template.convertAndSendToUser(id, "/topic/private-noti", message);*/
    }
}
