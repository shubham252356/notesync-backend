package com.notesync.backend.collaboration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time collaboration.
 *
 * <p>Provides STOMP over WebSocket endpoints. Real-time note update handlers
 * are placeholders to be implemented in a future iteration.
 *
 * <p>To migrate to a dedicated microservice, extract this module and configure
 * a message broker (e.g., RabbitMQ or Redis Pub/Sub) instead of the simple
 * in-memory broker.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-memory broker; replace with /topic prefix backed by RabbitMQ or Redis for production scale-out
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
