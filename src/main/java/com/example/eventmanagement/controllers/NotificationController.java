package com.example.eventmanagement.controllers;

import com.example.eventmanagement.entities.Message;
import com.example.eventmanagement.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

    // this is a controller
@Controller
public class NotificationController {
    @Autowired
    NotificationService notiService;

   /* @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message getMessage(final Message message){
        message.setMessageContent(HtmlUtils.htmlEscape(message.getMessageContent()));
        System.out.println("!!! Message from BACKEND " + message.getMessageContent());
        notiService.sendPublicNoti();
        return message;
    }*/

   /* @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public Message getPrivateMessage(final Message message, final Principal principal){
        message.setMessageContent(HtmlUtils.htmlEscape(principal.getName() +" %% "+message.getMessageContent()));
        System.out.println("!!! Private Message from BACKEND " + message.getMessageContent());
        notiService.sendPrivateNoti(principal.getName());
        return message;
    }*/

}
