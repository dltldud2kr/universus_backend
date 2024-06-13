package com.example.gazamung.chat.chatMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember,Long> {

    List<ChatMember> findAllByMemberIdx(long memberIdx);

    Optional<ChatMember> findByMemberIdxAndChatRoomIdAndChatRoomType(long memberIdx, long chatRoomId, int chatRoomType);

    ChatMember findByMemberIdxAndChatRoomId(long memberIdx, long chatRoomId);

    List<ChatMember> findByChatRoomIdAndChatRoomType(Long dynamicId, int i);


    List<ChatMember> findAllByChatRoomIdAndChatRoomType(Long chatRoomId, int i);

    void deleteByChatRoomTypeAndChatRoomId(int chatRoomType , Long chatRoomId);

    void deleteByChatRoomId(Long chatRoomId);

    List<ChatMember> findByMemberIdxAndChatRoomType(long senderIdx, int i);

    boolean existsByChatRoomIdAndMemberIdxAndChatRoomType(Long chatRoomId, long receiverIdx, int i);
}
