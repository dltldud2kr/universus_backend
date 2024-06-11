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
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@RequestMapping("/api/v1/univBoard")
public class UnivBoardController {

    private final UnivBoardService univBoardService;

    @Operation(summary = "게시글 작성", description = "" +
            "게시글을 작성합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- NOT_FOUND_CATEGORY: 해당 카테고리를 확인 할 수 없습니다." +
            "\n- NOT_FOUND_CLUB: 존재하지 않는 모임입니다."
    )
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

    @Operation(summary = "게시글 조회", description = "" +
            "게시글을 조회합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- NOT_FOUND_CATEGORY: 해당 카테고리를 확인 할 수 없습니다." +
            "\n- NOT_FOUND_CLUB: 존재하지 않는 모임입니다." +
            "\n- NOT_FOUND_BOARD: 해당 게시글을 찾을 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
    })
    @GetMapping("/info")
    public ResultDTO infoPost(@RequestParam Long univBoardId){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"게시글 정보 조회 완료.", univBoardService.infoPost(univBoardId));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "게시글 리스트 조회 ", description = "" +
            "게시글을 리스트를 조회합니다. categoryId : 0(전체), or 해당 카테고리 게시글만 조회" +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- NOT_FOUND_CATEGORY: 해당 카테고리를 확인 할 수 없습니다." +
            "\n- NOT_FOUND_CLUB: 존재하지 않는 모임입니다." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 리스트 조회 성공"),
    })
    @GetMapping("/list")
    public Object listPost(@RequestParam Long memberIdx, Long clubId, Long categoryId){
        try{
            List<InfoPost> listPosts = univBoardService.listPost(memberIdx, clubId, categoryId);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "게시글 리스트 조회 완료", listPosts);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "게시글 삭제 ", description = "" +
            "게시글을 삭제합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- NOT_FOUND_BOARD: 해당 게시글을 찾을 수 없습니다." +
            "\n- SERVER_ERROR: 요청중 서버 문제가 발생했습니다." +
            "\n- UNAUTHORIZED_USER: 권한이 없는 사용자입니다." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
    })
    @DeleteMapping("/delete")
    public ResultDTO deletePost(@RequestParam Long univBoardId, Long memberIdx){
        try{
            univBoardService.deletePost(univBoardId, memberIdx);
            return ResultDTO.of(true,ApiResponseCode.CREATED.getCode(), "게시글 삭제 완료", null);
        } catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "게시글 수정 ", description = "" +
            "게시글을 수정합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과"
//            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
//            "\n- NOT_FOUND_BOARD: 해당 게시글을 찾을 수 없습니다." +
//            "\n- SERVER_ERROR: 요청중 서버 문제가 발생했습니다." +
//            "\n- UNAUTHORIZED_USER: 권한이 없는 사용자입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
    })
    @PatchMapping("/modify")
    public ResultDTO modifyPost(PostDto dto){
        try{
            univBoardService.modifyPost(dto);
            return ResultDTO.of(true,ApiResponseCode.CREATED.getCode(), "게시글 수정 완료", null);
        } catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @PostMapping("/deletePostAdmin/{univBoardId}")
    public ResultDTO deletePostAdmin(@PathVariable Long univBoardId) {
        try {
            return ResultDTO.of(univBoardService.deletePostAdmin(univBoardId), ApiResponseCode.SUCCESS.getCode(), "게시글 삭제 완료", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


}
