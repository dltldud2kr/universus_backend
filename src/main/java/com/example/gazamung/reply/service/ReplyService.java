package com.example.gazamung.reply.service;

import com.example.gazamung.reply.dto.CreateDto;
import com.example.gazamung.reply.dto.InfoDto;
import com.example.gazamung.reply.dto.ModifyDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReplyService {
    Object createReply(CreateDto dto);

    List<InfoDto> listReply(Long univBoardId);

    Object deleteReply(Long replyId, Long memberIdx);

    Object modifyReply(ModifyDto dto);

    Object deleteReplyAdmin(Long replyId);
}
