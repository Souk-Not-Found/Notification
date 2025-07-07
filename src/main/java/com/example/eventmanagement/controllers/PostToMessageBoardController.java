package com.example.eventmanagement.controllers;
import com.example.eventmanagement.entities.Message;
import com.example.eventmanagement.services.NotificationService;
import com.example.eventmanagement.services.WebsocketService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;


@RestController
public class PostToMessageBoardController {
    @Autowired
    NotificationService notiService;

    @Autowired
    private WebsocketService service;

    @PostMapping("/posttomsgboard")
    public void  postMethodName(@RequestBody final Message message) {

        System.out.println("Service message sent : " + message.getMessageContent());
        service.postToMessageBoard(message.getMessageContent());
        notiService.sendPublicNoti();
    }


    @PostMapping("/postprivatetomsgboard/{id}")
    public void  postPrivateMessage(
            @PathVariable final String id,
            @RequestBody final Message message) {

        System.out.println("Service message sent : " + message.getMessageContent());
        service.postPrivateToMessageBoard(id, message.getMessageContent());
        notiService.sendPrivateNoti(id);
    }
}
