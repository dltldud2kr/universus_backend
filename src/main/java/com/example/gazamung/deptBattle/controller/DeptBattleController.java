package com.example.gazamung.deptBattle.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.deptBattle.dto.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.dto.DeptBattleCreateRequest;
import com.example.gazamung.deptBattle.service.DeptBattleService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
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

    @PostMapping("/attend")
    public ResultDTO attendBattle(@RequestBody DeptBattleAttendRequest request){

        try{
            return ResultDTO.of(deptBattleService.attend(request), ApiResponseCode.CREATED.getCode(),"대항전 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }



}
