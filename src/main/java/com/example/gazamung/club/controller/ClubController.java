package com.example.gazamung.club.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.dto.ClubRequest;
import com.example.gazamung.club.service.ClubService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/club")
public class ClubController {

    private final ClubService clubService;

    @PutMapping("/create")
    public ResultDTO create(ClubRequest.CreateClubRequestDto dto){
        try{
            Map<String, Object> result = clubService.create(dto);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 생성 완료.", result);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long moimId, Long memberIdx){
        try{
            clubService.delete(moimId, memberIdx);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "모임 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

//    @PatchMapping("/modify")
//    public ResultDTO modifyClub(ClubRequest.ModifyClubRequestDto dto){
//        try{
//            clubService.modify(moimId, memberIdx);
//            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "모임 삭제 완료.", null);
//        } catch(CustomException e){
//            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
//        }
//    }

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

