package com.example.gazamung.univBattle.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.univBattle.dto.UnivBattleAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.service.UnivBattleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/univBattle")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "대항전 API", description = "")
public class UnivBattleController {

    private  final UnivBattleService univBattleService;

    @PostMapping("/create")
    public ResultDTO createBattle(@RequestBody UnivBattleCreateRequest request) {

        try{
            return ResultDTO.of(univBattleService.create(request), ApiResponseCode.CREATED.getCode(),"대항전 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @PostMapping("/attend")
    public ResultDTO attendBattle(@RequestBody UnivBattleAttendRequest request){

        try{
            return ResultDTO.of(univBattleService.attend(request), ApiResponseCode.CREATED.getCode(),"대항전 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }



}
