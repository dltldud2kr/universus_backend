package com.example.gazamung.reply.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.reply.dto.CreateDto;
import com.example.gazamung.reply.dto.InfoDto;
import com.example.gazamung.reply.dto.ModifyDto;
import com.example.gazamung.reply.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "댓글 작성", description = "" +
            " 댓글 작성" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BOARD: 존재하지 않는 게시글입니다." +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/create")
    public ResultDTO createReply(@RequestBody CreateDto dto){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 작성 완료.", replyService.createReply(dto));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "게시글 댓글 조회", description = "" +
            " 해당 게시글의 댓글 조회 " +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BOARD: 존재하지 않는 게시글입니다." +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/list")
    public Object listReply(@RequestParam Long univBoardId){
        try {
            List<InfoDto> infoDtoList = replyService.listReply(univBoardId);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 조회 완료.", infoDtoList);
        } catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "댓글 수정", description = "" +
            " 댓글 수정 " +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 해당 정보를 찾을 수 없습니다." +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- NOT_FOUND_REPLY: 댓글을 찾을 수 없습니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PatchMapping("/modify")
    public Object modifyReply(@RequestBody ModifyDto dto){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 수정 완료.", replyService.modifyReply(dto));
        } catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "댓글 삭제", description = "" +
            " 댓글 삭제 " +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BOARD: 존재하지 않는 게시글입니다." +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- NOT_FOUND_REPLY: 댓글을 찾을 수 없습니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @DeleteMapping("/delete")
    public ResultDTO deleteReply(@RequestParam Long replyId, Long memberIdx){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "댓글 삭제 완료.", replyService.deleteReply(replyId, memberIdx));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



}
