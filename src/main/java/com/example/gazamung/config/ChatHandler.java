package com.example.gazamung.config;

import com.example.gazamung.ChatRoom.ChatMessage;
import com.example.gazamung.ChatRoom.ChatMessageService;
import lombok.RequiredArgsConstructor;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Log4j2
public class ChatHandler extends TextWebSocketHandler {
    private final ChatMessageService chatMessageService;

    private final Map<String, List<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload: " + payload);

        // 세션의 속성에서 memberIdx 값을 가져옵니다.
        String sessionMemberIdx = (String) session.getAttributes().get("memberIdx");
        Long memberIdx = Long.valueOf(sessionMemberIdx);

        // 세션의 URI에서 roomId를 추출합니다.
        String roomId = extractRoomId(session.getUri());

        // 해당 roomId의 채팅방에 있는 모든 세션에 메시지를 전송합니다.
        List<WebSocketSession> roomSessions = chatRooms.get(roomId);
        if (roomSessions != null) {
            for (WebSocketSession sess : roomSessions) {
                sess.sendMessage(message);
            }
        }

        // 채팅 메시지를 데이터베이스에 저장합니다.
        chatMessageService.saveChatMessage(roomId,memberIdx, payload);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // WebSocket 세션의 헤더를 추출합니다.
        Map<String, List<String>> headers = session.getHandshakeHeaders();
        // 사용자의 고유 ID인 memberIdx 값을 추출합니다.
        List<String> memberIdxValues = headers.get("memberIdx");
        if (memberIdxValues != null && !memberIdxValues.isEmpty()) {
            String memberIdx = memberIdxValues.get(0);

            // 추출된 memberIdx 값을 세션의 속성에 저장합니다.
            session.getAttributes().put("memberIdx", memberIdx);

            log.info("WebSocket 연결에 포함된 memberIdx: " + memberIdx);
        } else {
            // memberIdx 헤더가 없는 경우 처리할 내용을 작성합니다.
            log.warn("WebSocket 연결에 memberIdx 헤더가 없습니다.");
        }


        // 세션의 URI에서 roomId를 추출합니다.
        String roomId = extractRoomId(session.getUri());

        // 해당 roomId에 대한 채팅방이 없으면 새로 생성합니다.
        chatRooms.putIfAbsent(roomId, new CopyOnWriteArrayList<>());

        // 채팅방에 현재 세션을 추가합니다.
        List<WebSocketSession> roomSessions = chatRooms.get(roomId);
        roomSessions.add(session);

        log.info(session + " 클라이언트 접속 (roomId: " + roomId + ")");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션의 URI에서 roomId를 추출합니다.
        String roomId = extractRoomId(session.getUri());

        // 해당 roomId에 대한 채팅방에서 세션을 제거합니다.
        List<WebSocketSession> roomSessions = chatRooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.remove(session);
        }

        log.info(session + " 클라이언트 접속 해제 (roomId: " + roomId + ")");
    }

    private String extractRoomId(URI uri) {
        String path = uri.getPath();
        // URI에서 마지막 경로를 roomId로 추출합니다.
        return path.substring(path.lastIndexOf('/') + 1);
    }


}