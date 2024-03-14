package com.example.gazamung.club.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.dto.ClubJoinRequest;
import com.example.gazamung.club.dto.ClubRequest;
import com.example.gazamung.club.service.ClubService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
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
@Tag(name = "모임 API", description = "")
@RequestMapping("/api/v1/club")
public class ClubController {

    private final ClubService clubService;

    @Operation(summary = "모임 생성 ", description = "" +
            " 모임 생성." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/create")
    public ResultDTO create(ClubRequest.CreateClubRequestDto dto){
        try{
            Map<String, Object> result = clubService.create(dto);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 생성 완료.", result);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "모임 가입 ", description = "" +
            " 모임 가입." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/join")
    public ResultDTO join(@RequestBody ClubJoinRequest request){
        try{
            clubService.clubJoin(request);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "모임 삭제 ", description = "" +
            " 모임 삭제." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long moimId, Long memberIdx){
        try{
            clubService.delete(moimId, memberIdx);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "모임 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "모임 수정 ", description = "" +
            " 모임 수정." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PatchMapping("/modify")
    public ResultDTO modifyClub(ClubRequest.ModifyClubRequestDto dto){
        try{
            clubService.update(dto);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "모임 수정 완료.", null);
        } catch(CustomException e){
            if(e.getCustomErrorCode().getStatusCode().equals("ACCESS_DENIED")) {
                return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(),"모임 관리자만 수정할 수 있어요.", null);
            } else {
                return ResultDTO.of(false, ApiResponseCode.INTERNAL_SERVER_ERROR.getCode(), ApiResponseCode.INTERNAL_SERVER_ERROR.getMessage(), null);
            }
        }
    }

    @GetMapping("/list")
    public List<ClubDto> list(){
        try{
            List<ClubDto> requestDtoList = clubService.list();
            return requestDtoList;
        } catch (CustomException e) {
            return (List<ClubDto>) ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), "모임 리스트 조회 완료");
        }
    }

    @GetMapping("/info")
    public ResultDTO info(@RequestParam Long clubId){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 조회 완료.", clubService.info(clubId));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }
}

