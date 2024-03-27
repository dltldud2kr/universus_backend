package com.example.gazamung.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        // ws://localhost:8080/ws/chat/{roomId} 형식의 엔드포인트를 추가합니다.
        registry.addHandler(chatHandler, "/ws/chat/{roomType}/{roomId}")
                .setAllowedOrigins("*");
    }
}