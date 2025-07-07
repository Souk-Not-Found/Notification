package com.example.eventmanagement.handlers;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.util.UUID;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Map;

public class UserHandShakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        final String randomId = UUID.randomUUID().toString();
        System.out.println("USER ID :  "+ randomId);

        return null ;
    }



}
