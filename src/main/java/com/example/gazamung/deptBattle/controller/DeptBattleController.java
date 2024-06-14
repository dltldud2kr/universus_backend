package com.example.gazamung.deptBattle.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.deptBattle.dto.*;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.deptBattle.service.DeptBattleService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import com.example.gazamung.univBattle.dto.MatchResultRequest;
import com.example.gazamung.univBattle.dto.MatchResultResponse;
import com.example.gazamung.univBattle.dto.UnivBattleListRes;
import com.example.gazamung.univBattle.entity.UnivBattle;
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
@RequestMapping("/api/v1/deptBattle")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대항전(과 vs 과) API", description = "")
public class DeptBattleController {

    private  final DeptBattleService deptBattleService;

    @Operation(summary = "과 vs 과 대항전 생성 ", description = "" +
            " 모임 가입." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_USER: 존재하지 않는 회원입니다." +
            "\n- NOT_FOUND: 존재하지 않는 회원입니다." +
            "\n- NOT_FOUND_DEPARTMENT: 존재하지 않는 회원입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/create")
    public ResultDTO createBattle(@RequestBody DeptBattleCreateRequest request) {

        try{
            return ResultDTO.of(deptBattleService.create(request), ApiResponseCode.CREATED.getCode(),"대항전 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "과 vs 과 대항전 대표 참가 ", description = "" +
            " 대항전 과 대표자 참가." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- NOT_FOUND_USER: 존재하지 않는 회원입니다." +
            "\n- SAME_UNIVERSITY: 같은 대학교는 참가할 수 없습니다." +
            "\n- NOT_SAME_UNIVERSITY: 같은 대학교는 참가할 수 없습니다." +
            "\n- REPRESENTATIVE_ALREADY_EXISTS: 같은 대학교는 참가할 수 없습니다." +
            "\n- SAME_DEPARTMENT: 같은 대학교는 참가할 수 없습니다." +
            "\n- NOT_FOUND_DEPARTMENT: 같은 대학교는 참가할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/repAttend")
    public ResultDTO representativeAttendBattle(@RequestBody DeptGuestLeaderAttendRequest request){
        try{
            return ResultDTO.of(deptBattleService.GuestLeaderAttend(request), ApiResponseCode.CREATED.getCode(),"대항전 참가팀 대표자 참가완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "과 vs 과 대항전 일반 참가 ", description = "" +
            " 모임 가입." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- NOT_FOUND_USER: 존재하지 않는 회원입니다." +
            "\n- EXCEEDED_TOTAL_CAPACITY: 대항전 총 참가 인원이 초과하였습니다." +
            "\n- EXCEEDED_DEPT_CAPACITY: 대항전 과 최대 참가 인원이 초과하였습니다." +
            "\n- INVALID_INVITE_CODE: 참가 코드가 유효하지 않습니다." +
            "\n- ALREADY_ATTENDED: 이미 참가한 회원입니다." +
            "\n- ALREADY_IN_PROGRESS: 이미 진행중인 경기입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/attend")
    public ResultDTO attendBattle(@RequestBody DeptBattleAttendRequest request){

        try{
            return ResultDTO.of(deptBattleService.attend(request), ApiResponseCode.CREATED.getCode(),"대항전 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }


    @Operation(summary = "과 vs 과 대항전 카테고리별  리스트 ", description = "PARAM 유효값 : 0,1,2,3" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- 0 (ALL): 전체 리스트" +
            "\n- 1 (WAITING): 대기중 리스트" +
            "\n- 2 (IN_PROGRESS): 진행중 리스트" +
            "\n- 3 (COMPLETED): 종료 리스트" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/list")
    public ResultDTO deptBattleList(@RequestParam(required = false)int status ){

        List<DeptBattle> deptBattleList = deptBattleService.list(status);
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "대항전리스트 조회 성공.", deptBattleList);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "해당 학과 경기 리스트 ", description = "PARAMETER: deptId " +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- 0 (ALL): 전체 리스트" +
            "\n- 1 (WAITING): 대기중 리스트" +
            "\n- 2 (IN_PROGRESS): 진행중 리스트" +
            "\n- 3 (COMPLETED): 종료 리스트" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/dList")
    public ResultDTO eachDeptBattleList(@RequestParam Long univId, @RequestParam Long deptId ){

        try {
            List<DeptBattleListRes> deptBattleListResList = deptBattleService.dList(univId,deptId);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "해당 대항전 매치 리스트 조회 성공.", deptBattleListResList);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "과 대항전 정보 ", description = "" +
            " 대항전 정보를 반환합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- NOT_FOUND_UNIVERSITY: 존재하지 않는 대항전입니다." +
            "\n- NOT_FOUND_DEPARTMENT: 존재하지 않는 대항전입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/info")
    public ResultDTO<Map<String, Object>> deptBattleInfo(@RequestParam long deptBattleId){
        try {
            Map<String, Object> result = deptBattleService.info(deptBattleId);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "대항전 정보", result);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "과 대항전 시작 ", description = "" +
            " 대항전 경기를 진행상태로 변경합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- CANNOT_START_MATCH: 존재하지 않는 대항전입니다." +
            "\n- INSUFFICIENT_MATCH_PLAYERS: 존재하지 않는 대항전입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/matchStart")
    public ResultDTO deptBattleMatchStart(@RequestParam long deptBattleId){
        try {
            return ResultDTO.of(deptBattleService.matchStart(deptBattleId), ApiResponseCode.SUCCESS.getCode(), "대항전 시작", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "과 대항전 결과 전송(주최자) ", description = "" +
            " 대항전 결과를 참가팀측에 전송합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- NOT_IN_PROGRESS: 존재하지 않는 대항전입니다." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/resultReq")
    public ResultDTO deptMatchResultReq(@RequestBody DeptMatchResultReq dto){
        try {
            return ResultDTO.of(deptBattleService.matchResultReq(dto), ApiResponseCode.SUCCESS.getCode(), "대항전 결과전송", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "과 대항전 결과 확인(참가자) ", description = "" +
            " 주최자측에서 보낸 결과를 확인 후 경기를 종료 또는 재요청합니다. " +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- ALREADY_END_MATCH: 존재하지 않는 대항전입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/resultRes")
    public ResultDTO deptMatchResultRes(@RequestBody DeptMatchResultRes dto){
        try {
            return ResultDTO.of(deptBattleService.matchResultRes(dto), ApiResponseCode.SUCCESS.getCode(), "대항전 결과응답", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



}
