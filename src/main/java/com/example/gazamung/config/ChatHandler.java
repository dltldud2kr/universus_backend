package com.example.gazamung.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Log4j2
public class ChatHandler extends TextWebSocketHandler {

    private  List<WebSocketSession> list = new ArrayList<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : " + payload);

        for(WebSocketSession sess: list) {
            sess.sendMessage(message);
        }
    }

    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        //WebSocketSession 리스트에 현재 채팅방 세션을 추가.
        list.add(session);

        // 세션 ID 를 받는다.



        log.info(session + " 클라이언트 접속");
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        log.info(session + " 클라이언트 접속 해제");
        list.remove(session);
    }

    // 핸들러를 제공하여 핸드셰이크를 수행하는 메서드
    public WebSocketSession createWebSocketSession() throws URISyntaxException, IOException {
        try {
            // 웹소켓 클라이언트 연결을 생성하고 핸들러를 제공합니다.
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketSession session = client.doHandshake(new TextWebSocketHandler() {
                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    // 텍스트 메시지 처리 로직을 작성합니다.
                }
            }, "ws://localhost:8080/chat").get();

            return session;
        } catch (InterruptedException | ExecutionException e) {
            // 예외 처리 로직을 작성합니다.
            log.error("Error during websocket handshake: " + e.getMessage());
            throw new IOException("Error during websocket handshake", e);
        }
    }

}