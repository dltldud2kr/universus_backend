package com.example.gazamung.config;

import com.example.gazamung.ChatRoom.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
        String room = extractRoom(session.getUri());

        String roomId = null;
        int battleType = -1;

        // "/" 기준으로 문자열을 분할합니다.
        String[] parts = room.split("/");
        if (parts.length == 2) {
            // 분할된 결과 배열의 길이가 2여야 합니다.
            String battleTypeStr = parts[0];
            String roomIdStr = parts[1];

            try {
                // 문자열을 정수로 변환합니다.
                battleType = Integer.parseInt(battleTypeStr);
                roomId = roomIdStr;
            } catch (NumberFormatException e) {
                // 정수로 변환할 수 없는 경우, 예외 처리를 합니다.
                System.err.println("Invalid battleType format: " + battleTypeStr);
            }

            // 추출된 값들을 처리합니다.
            System.out.println("Battle Type: " + battleType);
            System.out.println("Room ID: " + roomId);
        } else {
            // 유효한 형식이 아닐 경우 예외 처리나 다른 로직을 수행할 수 있습니다.
            System.err.println("Invalid room format: " + room);
        }

        // 해당 roomId의 채팅방에 있는 모든 세션에 메시지를 전송합니다.
        List<WebSocketSession> roomSessions = chatRooms.get(room);
        if (roomSessions != null) {
            for (WebSocketSession sess : roomSessions) {
                sess.sendMessage(message);
            }
        }

        // 채팅 메시지를 데이터베이스에 저장합니다.
        chatMessageService.saveChatMessage(battleType, roomId, memberIdx, payload);
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
        String room = extractRoom(session.getUri());


//        Long chatRoomId = Long.valueOf(room);
        // 해당 roomId에 대한 채팅방이 없으면 새로 생성합니다.
        chatRooms.putIfAbsent(room, new CopyOnWriteArrayList<>());

        // 채팅방에 현재 세션을 추가합니다.
        List<WebSocketSession> roomSessions = chatRooms.get(room);
        roomSessions.add(session);

        // 이전 채팅 내용을 DB에서 가져옵니다.
//        List<ChatMessage> chatMessageList = chatMessageService.chatList(chatRoomId);

        // 가져온 채팅 내용을 클라이언트에게 전송합니다.
//        ObjectMapper mapper = new ObjectMapper();
//        String chatMessageJson = mapper.writeValueAsString(chatMessageList);
//        session.sendMessage(new TextMessage(chatMessageJson));

        log.info(session + " 클라이언트 접속 (roomId: " + room + ")");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션의 URI에서 roomId를 추출합니다.
        String roomId = extractRoom(session.getUri());

        // 해당 roomId에 대한 채팅방에서 세션을 제거합니다.
        List<WebSocketSession> roomSessions = chatRooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.remove(session);
        }
        log.info(session + " 클라이언트 접속 해제 (roomId: " + roomId + ")");
    }

    private String extractRoom(URI uri) {
        String path = uri.getPath();
        // URI에서 끝에서 두 번째 경로를 추출합니다.
        String[] parts = path.split("/");
        if (parts.length >= 2) {
            // parts 배열의 길이가 2 이상이라면, 끝에서 두 번째 값이 battleType이 될 것입니다.
            String battleType = parts[parts.length - 2];
            String roomId = parts[parts.length - 1];
            return battleType + "/" + roomId;
        } else {
            // 경로가 충분히 깊지 않을 경우 null이나 예외 처리를 수행할 수 있습니다.
            return null;
        }
    }

}