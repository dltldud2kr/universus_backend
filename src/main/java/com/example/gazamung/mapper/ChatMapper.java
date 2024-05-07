package com.example.gazamung.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMapper {
    String findLatestMessageContentByChatRoomId(Long chatRoomId);
}
