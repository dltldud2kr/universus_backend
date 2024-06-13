package com.example.gazamung.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ChatMapper {
    //    String findLatestMessageContentByChatRoomId(Long chatRoomId);
    Map<String, Object> findLatestMessageByChatRoomId(Long chatRoomId);

    int countChatRoomsByMembersAndType(@Param("senderIdx") Long senderIdx, @Param("receiverIdx") Long receiverIdx, @Param("chatRoomType") int chatRoomType);


}
