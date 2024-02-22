package com.example.gazamung.like.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.like.dto.LikeRequestDto;
import com.example.gazamung.like.repository.LikeRepository;
import com.example.gazamung.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/add")
    public ResultDTO addLike(@RequestBody LikeRequestDto likeRequestDto){
        try{
            return ResultDTO.of(likeService.addLike(likeRequestDto), ApiResponseCode.CREATED.getCode(),"좋아요", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @DeleteMapping("/delete")
    public ResultDTO deleteLike(@RequestBody LikeRequestDto likeRequestDto){
        try{
            return ResultDTO.of(likeService.deleteLike(likeRequestDto), ApiResponseCode.CREATED.getCode(),"좋아요 취소", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



}
