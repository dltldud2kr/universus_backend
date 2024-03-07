package com.example.gazamung.group.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.group.dto.GroupDto;
import com.example.gazamung.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/moim")
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/create")
    public ResultDTO create(@RequestBody GroupDto dto, Principal principal){
        try{
            return ResultDTO.of(groupService.create(dto), ApiResponseCode.CREATED.getCode(),"모임 생성 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }


    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long moimId, Long memberIdx){
        try{
            return ResultDTO.of(groupService.delete(moimId, memberIdx), ApiResponseCode.SUCCESS.getCode(), "모임 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }






}
