package com.example.gazamung.univBoard.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.univBoard.dto.InfoPost;
import com.example.gazamung.univBoard.dto.PostDto;
import com.example.gazamung.univBoard.service.UnivBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "게시판 API", description = "")
@RequestMapping("/api/v1/univBoard")
public class UnivBoardController {

    private final UnivBoardService univBoardService;

    @Operation(summary = "게시글 작성", description = "" +
            "게시글을 작성합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 403: 회원정보 인증 실패" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
    })
    @PostMapping("/create")
    public ResultDTO createPost(PostDto dto){
        try {
            Map<String, Object> result = univBoardService.createPost(dto);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "게시글 작성 완료", result);
        } catch (CustomException e) {
            return  ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "게시글 정보 조회 ", description = "" +
            " 게시글 정보 조회." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 해당 정보를 찾을 수 없습니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/info")
    public ResultDTO infoPost(@RequestParam Long univBoardId){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"게시글 정보 조회 완료.", univBoardService.infoPost(univBoardId));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/list")
    public Object listPost(@RequestParam Long memberIdx, Long clubId){
        try{
            List<InfoPost> listPosts = univBoardService.listPost(memberIdx, clubId);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "게시글 리스트 조회 완료", listPosts);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


}
