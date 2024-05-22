package com.example.gazamung.rank.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.department.entity.Department;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.rank.dto.UnivRankRes;
import com.example.gazamung.rank.entity.Rank;
import com.example.gazamung.rank.service.RankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = " 랭킹 API", description = "")
public class RankController {

    private final RankService rankService;

    @GetMapping("/list")
    public ResultDTO rankList(@RequestParam(required = false) Long eventId) {

        try{
            List<UnivRankRes> list = rankService.rankList(eventId);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"랭킹 리스트 조회 성공", list);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

}
