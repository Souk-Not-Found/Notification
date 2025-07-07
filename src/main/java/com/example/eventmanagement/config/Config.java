package com.example.eventmanagement.config;

import com.example.eventmanagement.handlers.UserHandShakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class Config implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:4200")
                .setHandshakeHandler(new UserHandShakeHandler())
                .withSockJS();
    }
}
