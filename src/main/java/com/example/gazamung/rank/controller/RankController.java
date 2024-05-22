package com.example.gazamung.rank.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.department.entity.Department;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.rank.dto.UnivRankRes;
import com.example.gazamung.rank.entity.Rank;
import com.example.gazamung.rank.service.RankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "랭킹 점수 리스트 ", description = "PARAMETER: eventId  " +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과"
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
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
