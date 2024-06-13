package com.example.gazamung.chat.chatRoom;

import com.example.gazamung.chat.chatRoom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    Optional<ChatRoom> findByChatRoomIdAndChatRoomType(Long chatRoomId, int chatRoomType);

    ChatRoom findByChatRoomTypeAndDynamicId(int chatRoomType, long dynamicId);



}
