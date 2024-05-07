package com.example.gazamung.deptBattle.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.deptBattle.dto.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.dto.DeptBattleCreateRequest;
import com.example.gazamung.deptBattle.dto.DeptGuestLeaderAttendRequest;
import com.example.gazamung.deptBattle.service.DeptBattleService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deptBattle")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대항전(과 vs 과) API", description = "")
public class DeptBattleController {

    private  final DeptBattleService deptBattleService;

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
            "\n- SAME_UNIVERSITY: 같은 대학교는 참가할 수 없습니다." )
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


    @PostMapping("/attend")
    public ResultDTO attendBattle(@RequestBody DeptBattleAttendRequest request){

        try{
            return ResultDTO.of(deptBattleService.attend(request), ApiResponseCode.CREATED.getCode(),"대항전 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }



}
