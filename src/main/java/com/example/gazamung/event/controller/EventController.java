package com.example.gazamung.event.controller;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.dto.ClubRequest;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.event.dto.EventDto;
import com.example.gazamung.event.dto.ListDto;
import com.example.gazamung.event.entity.Event;
import com.example.gazamung.event.service.EventService;
import com.example.gazamung.exception.CustomException;
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
@Tag(name = "종목 카테고리 API", description = "")
@RequestMapping("/api/v1/event")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "종목 생성 ", description = "" +
            " 종목 생성." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/create")
    public ResultDTO create(@RequestBody EventDto dto) {
        try {
            return ResultDTO.of(eventService.create(dto), ApiResponseCode.CREATED.getCode(), "종목 생성 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "종목 삭제 ", description = "" +
            " 종목 삭제." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestBody EventDto dto){
        try{
            return ResultDTO.of(eventService.delete(dto), ApiResponseCode.SUCCESS.getCode(), "종목 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "종목 수정 ", description = "" +
            " 종목 수정." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/modify")
    public ResultDTO modifyClub(@RequestBody EventDto dto){
        try{
            return ResultDTO.of(eventService.update(dto), ApiResponseCode.SUCCESS.getCode(), "모임 수정 완료.", null);
        }  catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "종목 리스트 조회 ", description = "" +
            " 종목 리스트 조회." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/list")
    public List<ListDto> list(){
        try{
            List<ListDto> listDtos = eventService.list();
            return listDtos;
        } catch (CustomException e) {
            return (List<ListDto>) ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), "모임 전체 리스트 조회 완료");
        }
    }
}
