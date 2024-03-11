package com.example.gazamung.club.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/club")
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/create")
    public ResultDTO create(@RequestBody ClubDto dto){
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

    @GetMapping("/list")
    public List<ClubDto> list(){
        try{
            List<ClubDto> clubDtoList = clubService.list();
            return  clubDtoList;
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

