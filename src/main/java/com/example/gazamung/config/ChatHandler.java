package com.example.gazamung.config;

import com.example.gazamung.chatRoom.ChatMessage;
import com.example.gazamung.chatRoom.ChatMessageService;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Log4j2
public class ChatHandler extends TextWebSocketHandler {
    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;

    private final Map<String, List<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();


    /**
     * WebSocket으로 수신된 텍스트 메시지를 처리합니다.
     * 클라이언트로부터 받은 메시지를 파싱하여 필요한 작업을 수행하고,
     * 채팅 메시지를 데이터베이스에 저장한 뒤, 해당 채팅방에 있는 모든 클라이언트에게 전송합니다.
     *
     * @param session WebSocket 세션
     * @param message WebSocket으로부터 수신된 텍스트 메시지
     * @throws Exception 예외 발생 시
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 클라이언트로부터 수신한 메시지를 추출.
        String payload = message.getPayload();

        // WebSocket 세션의 속성에서 memberIdx 값을 가져옴.
        String sessionMemberIdx = (String) session.getAttributes().get("memberIdx");
        Long memberIdx = Long.valueOf(sessionMemberIdx);

        // memberIdx 이용하여 사용자의 닉네임을 데이터베이스에서 조회.
        String nickname = memberRepository.findById(memberIdx).get().getNickname();

        // WebSocket 세션의 URI에서 roomId를 추출.
        String room = extractRoom(session.getUri());

        // roomId와 battleType을 초기화.
        String roomId = null;
        int battleType = -1;


        // URI에서 roomId와 battleType을 "/" 기준으로 문자열을 분할.
        String[] parts = room.split("/");
        if (parts.length == 2) {
            // 분할된 결과 배열의 길이가 2여야 합니다.
            String battleTypeStr = parts[0];
            String roomIdStr = parts[1];

            try {
                // 문자열을 정수로 변환.
                battleType = Integer.parseInt(battleTypeStr);
                roomId = roomIdStr;
            } catch (NumberFormatException e) {
                // 정수로 변환할 수 없는 경우, 예외 처리를 합니다.
                log.error("Invalid battleType format: " + battleTypeStr);
            }

            // 추출된 값들을 처리.
            log.info("Battle Type: " + battleType);
            log.info("Room ID: " + roomId);
        } else {
            // URI 형식이 잘못된 경우, 예외 처리를 합니다.
            throw new CustomException(CustomExceptionCode.INVALID_URI);
        }

        // 채팅 메시지를 데이터베이스에 저장.
        ChatMessage savedChatMessage = chatMessageService.saveChatMessage(battleType, roomId, memberIdx, payload,nickname);


        // 저장된 DB 데이터를 JSON 형태로 변환.
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode chatMessageArray = mapper.createArrayNode();
        ObjectNode messageNode = mapper.createObjectNode();

        messageNode.put("nickname", savedChatMessage.getNickname());
        messageNode.put("content", savedChatMessage.getContent());
        messageNode.put("memberIdx", savedChatMessage.getMemberIdx());

        // LocalDateTime을 문자열로 변환하여 JSON에 추가.
        // (LocalDateTime 이 JAVA8 버전 이후 변환이 안되어 직접 변환)
        messageNode.put("regDt", formatLocalDateTime(savedChatMessage.getRegDt()));

        chatMessageArray.add(messageNode);
        String chatMessageJson = mapper.writeValueAsString(chatMessageArray);

        // 해당 roomId의 채팅방에 있는 모든 세션에 메시지를 전송.
        List<WebSocketSession> roomSessions = chatRooms.get(room);
        if (roomSessions != null) {
            for (WebSocketSession sess : roomSessions) {
                sess.sendMessage(new TextMessage(chatMessageJson));
            }
        }

    }


    /**
     * WebSocket 연결이 확립된 후 호출됩니다.
     * 클라이언트가 연결되면, 해당 클라이언트의 고유한 memberIdx를 추출하여 WebSocket 세션의 속성에 저장하고,
     * 채팅방에 입장한 사용자에 대한 정보를 브로드캐스트하여 다른 클라이언트에게 알립니다.
     *
     * @param session WebSocket 세션
     * @throws Exception 예외 발생 시
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // WebSocket 세션의 헤더에서 memberIdx 값을 추출.
        Map<String, List<String>> headers = session.getHandshakeHeaders();

        List<String> memberIdxValues = headers.get("memberIdx");
        if (memberIdxValues != null && !memberIdxValues.isEmpty()) {
            String memberIdx = memberIdxValues.get(0);
            Long parseIdx = Long.valueOf(memberIdx);


            // 추출된 memberIdx 값을 세션의 속성에 저장.
            session.getAttributes().put("memberIdx", memberIdx);
            log.info("WebSocket 연결에 포함된 memberIdx: " + memberIdx);

            String nickname = memberRepository.findById(parseIdx).get().getNickname();
            String room = extractRoom(session.getUri());

            // 채팅방에 입장 메시지를 브로드캐스트.
            List<WebSocketSession> roomSessions = chatRooms.get(room);
            if (roomSessions != null) {
                // 입장 메시지 생성
                String entryMessage = nickname + "님이 입장하셨습니다.";
                TextMessage entryTextMessage = new TextMessage(entryMessage);

                // 해당 채팅방에 참가한 모든 세션에 입장 메시지 전송
                for (WebSocketSession sess : roomSessions) {
                    sess.sendMessage(entryTextMessage);
                }
            }

        } else {
            // memberIdx 헤더가 없는 경우 예외처리.
            throw new CustomException(CustomExceptionCode.NOT_FOUND_HEADER_DATA);
        }


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
        } else {
            // 유효한 형식이 아닐 경우 예외 처리나 다른 로직을 수행할 수 있습니다.
            System.err.println("Invalid room format: " + room);
        }

        // 해당 roomId에 대한 채팅방이 없으면 새로 생성합니다.
        chatRooms.putIfAbsent(room, new CopyOnWriteArrayList<>());

        // 채팅방에 현재 세션을 추가합니다.
        List<WebSocketSession> roomSessions = chatRooms.get(room);
        roomSessions.add(session);

        // 이전 채팅 내용을 DB에서 가져옵니다.
        List<ChatMessage> chatMessageList = chatMessageService.chatList(roomId);

        // 가져온 채팅 내용을 클라이언트에게 전송하기 위해 JSON 형식으로 변환합니다.
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode chatMessageArray = mapper.createArrayNode();
        for (ChatMessage chatMessage : chatMessageList) {
            ObjectNode messageNode = mapper.createObjectNode();
            messageNode.put("nickname", chatMessage.getNickname());
            messageNode.put("content", chatMessage.getContent());
            messageNode.put("memberIdx", chatMessage.getMemberIdx());
            // LocalDateTime을 문자열로 변환하여 JSON에 추가합니다.
            messageNode.put("regDt", formatLocalDateTime(chatMessage.getRegDt()));
            chatMessageArray.add(messageNode);
        }
        // JSON 배열을 문자열로 직렬화하여 클라이언트에게 전송합니다.
        String chatMessageJson = mapper.writeValueAsString(chatMessageArray);
        session.sendMessage(new TextMessage(chatMessageJson));

        log.info(session + " 클라이언트 접속 (roomId: " + room + ")");
    }


    /**
     * WebSocket 연결이 닫힌 후 호출됩니다.
     * 클라이언트가 연결을 닫으면, 해당 클라이언트를 채팅방에서 제거합니다.
     *
     * @param session WebSocket 세션
     * @param status  클라이언트 연결 종료 상태
     * @throws Exception 예외 발생 시
     */
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
            // parts 배열의 길이가 2 이상이라면, 끝에서 두 번째 값이 battleType이 됨.
            String battleType = parts[parts.length - 2];
            String roomId = parts[parts.length - 1];
            return battleType + "/" + roomId;
        } else {
            // 경로가 충분히 깊지 않을 경우 null이나 예외 처리를 수행할 수 있음.
            return null;
        }
    }


    // ChatMessage 객체의 LocalDateTime 문자열로 변환하는 메소드
    private String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

}