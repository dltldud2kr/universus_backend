package com.example.gazamung.chat.chatMember;

import com.example.gazamung.chat.chatRoom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember,Long> {

    List<ChatMember> findAllByMemberIdx(long memberIdx);

    Optional<ChatMember> findByMemberIdxAndChatRoomIdAndChatRoomType(long memberIdx, long chatRoomId, int chatRoomType);

}
