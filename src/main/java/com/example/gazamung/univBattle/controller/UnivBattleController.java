package com.example.gazamung.univBattle.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung._enum.Status;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.univBattle.dto.AttendRequest;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.service.UnivBattleService;
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
@RequestMapping("/api/v1/univBattle")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대항전(대학 vs 대학) API", description = "")
public class UnivBattleController {

    private  final UnivBattleService univBattleService;
    @Operation(summary = "대항전 생성 ", description = "" +
            " 모임 가입." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_USER: 존재하지 않는 회원입니다." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/create")
    public ResultDTO createBattle(@RequestBody UnivBattleCreateRequest request) {

        try{
            return ResultDTO.of(univBattleService.create(request), ApiResponseCode.CREATED.getCode(),"대항전 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "대항전 대표 참가 ", description = "" +
            " 모임 가입." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- NOT_FOUND_USER: 존재하지 않는 회원입니다." +
            "\n- SAME_UNIVERSITY: 같은 대학교는 참가할 수 없습니다." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/repAttend")
    public ResultDTO representativeAttendBattle(@RequestBody GuestLeaderAttendRequest request){
        try{
            return ResultDTO.of(univBattleService.GuestLeaderAttend(request), ApiResponseCode.CREATED.getCode(),"대항전 참가팀 대표자 참가완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "대항전 일반 참가 ", description = "" +
            " 모임 가입." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND_BATTLE: 존재하지 않는 대항전입니다." +
            "\n- NOT_FOUND_USER: 존재하지 않는 회원입니다." +
            "\n- EXCEEDED_TOTAL_CAPACITY: 대항전 총 참가 인원이 초과하였습니다." +
            "\n- EXCEEDED_UNIV_CAPACITY: 대항전 대학별 참가 인원이 초과하였습니다." +
            "\n- INVALID_INVITE_CODE: 참가 코드가 유효하지 않습니다." +
            "\n- ALREADY_ATTENDED: 이미 참가한 회원입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/attend")
    public ResultDTO attendBattle(@RequestBody AttendRequest request) {

        try {
            return ResultDTO.of(univBattleService.attend(request), ApiResponseCode.CREATED.getCode(), "대항전 참가 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "대항전 리스트 ", description = "PARAM 유효값 : 0,1,2,3" +
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
    public ResultDTO battleList(@RequestParam(required = false)int status ){

        List<UnivBattle> univBattleList = univBattleService.list(status);
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "대항전리스트 조회 성공.", univBattleList);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/info")
    public ResultDTO<Map<String, Object>> univBattleInfo(@RequestParam long univBattleId){
        try {
            Map<String, Object> result = univBattleService.info(univBattleId);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "대항전 정보", result);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


}
