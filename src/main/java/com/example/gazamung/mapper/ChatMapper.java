package com.example.gazamung.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ChatMapper {
    //    String findLatestMessageContentByChatRoomId(Long chatRoomId);
    Map<String, Object> findLatestMessageByChatRoomId(Long chatRoomId);

}
