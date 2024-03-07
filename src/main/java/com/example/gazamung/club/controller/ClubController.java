package com.example.gazamung.club.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/moim")
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/create")
    public ResultDTO create(@RequestBody ClubDto dto, Principal principal){
        try{
            return ResultDTO.of(clubService.create(dto), ApiResponseCode.CREATED.getCode(),"모임 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }


    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long moimId, Long memberIdx){
        try{
            return ResultDTO.of(clubService.delete(moimId, memberIdx), ApiResponseCode.SUCCESS.getCode(), "모임 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }






}
