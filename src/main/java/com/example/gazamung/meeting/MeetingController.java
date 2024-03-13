package com.example.gazamung.meeting;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.category.dto.CategoryDto;
import com.example.gazamung.category.service.CategoryService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.meeting.dto.MeetingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meeting")
public class MeetingController {

    private final MeetingService meetingService;


    @Operation(summary = "정기모임 생성", description = "" +
            "정기모임 생성합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 403: 회원정보 인증 실패" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 생성 성공"),
    })
    @PostMapping("/create")
    public ResultDTO createMeeting(MeetingRequest.CreateMeetingRequestDto request) {
        try{
            Map<String, Object> result = meetingService.create(request);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"정모 생성 완료.", result);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }

    }

}