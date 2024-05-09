package com.example.gazamung.chat.chatMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember,Long> {

    List<ChatMember> findAllByMemberIdx(long memberIdx);

    Optional<ChatMember> findByMemberIdxAndChatRoomIdAndChatRoomType(long memberIdx, long chatRoomId, int chatRoomType);

    ChatMember findByMemberIdxAndChatRoomId(long memberIdx, long chatRoomId);

    ChatMember findByChatRoomIdAndChatRoomType(Long dynamicId, int i);

    void delete(Optional<ChatMember> chatMember);
}
