package com.example.gazamung.moim.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.moim.dto.MoimDto;
import com.example.gazamung.moim.service.MoimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/moim")
public class MoimController {

    private final MoimService moimService;

    @PostMapping("/create") //모임 생성
    public ResultDTO create(@RequestBody MoimDto dto){
        try{
            return ResultDTO.of(moimService.create(dto), ApiResponseCode.CREATED.getCode(),"모임 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @GetMapping("/info")    //모임 정보 조회
    public List<MoimDto> info(@RequestParam Long moimId){
        try{
            List<MoimDto> list = moimService.info(moimId);
            return list;
        } catch (Exception e){
            return (List<MoimDto>) ResultDTO.of(false, ApiResponseCode.INTERNAL_SERVER_ERROR.getCode(), "모임 조회 실패.", null);
        }
    }

    @DeleteMapping("/delete")   //모임 삭제
    public ResultDTO delete(@RequestParam Long moimId, Long memberIdx){
        try{
            return ResultDTO.of(moimService.delete(moimId, memberIdx), ApiResponseCode.SUCCESS.getCode(), "모임 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @PostMapping("/update") // 모임 수정
    public ResultDTO update(@RequestParam Long moimId, Long memberIdx, @RequestBody MoimDto dto) {
        try {
            return ResultDTO.of(moimService.update(moimId, memberIdx, dto), ApiResponseCode.SUCCESS.getCode(), "모임 수정 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

}
