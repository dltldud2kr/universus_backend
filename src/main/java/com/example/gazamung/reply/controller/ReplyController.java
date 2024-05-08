package com.example.gazamung.reply.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.reply.dto.CreateDto;
import com.example.gazamung.reply.dto.InfoDto;
import com.example.gazamung.reply.dto.ModifyDto;
import com.example.gazamung.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reply")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "댓글 API", description = "")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/create")
    public ResultDTO createReply(@RequestBody CreateDto dto){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 작성 완료.", replyService.createReply(dto));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/list")
    public Object listReply(@RequestParam Long univBoardId){
        try {
            List<InfoDto> infoDtoList = replyService.listReply(univBoardId);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 조회 완료.", infoDtoList);
        } catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @PatchMapping("/modify")
    public Object modifyReply(@RequestBody ModifyDto dto){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 수정 완료.", replyService.modifyReply(dto));
        } catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @DeleteMapping("/delete")
    public ResultDTO deleteReply(@RequestParam Long replyId, Long memberIdx){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 삭제 완료.", replyService.deleteReply(replyId, memberIdx));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



}
