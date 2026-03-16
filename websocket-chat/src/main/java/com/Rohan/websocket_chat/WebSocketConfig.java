package com.Rohan.websocket_chat;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    public final ChatWebSocketHandler ChatWebSockeHandler;

    public WebSocketConfig(ChatWebSocketHandler chatWebSockeHandler) {
        ChatWebSockeHandler = chatWebSockeHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ChatWebSockeHandler, "/chat").setAllowedOrigins("*");
    }
}
