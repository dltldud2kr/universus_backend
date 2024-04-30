package com.example.gazamung.chat.chatMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findAllByChatRoomIdOrderByRegDtDesc(Long chatRoomId);

}
