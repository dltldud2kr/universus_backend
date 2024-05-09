package com.example.gazamung.club.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.club.dto.*;
import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.service.ClubService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "모임 API", description = "")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@RequestMapping("/api/v1/club")
public class ClubController {

    private final ClubService clubService;

    @Operation(summary = "모임 생성 ", description = "" +
            " 모임 생성." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
//    @PostMapping(value ="/create",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResultDTO create(@RequestPart("dto") ClubRequest.CreateClubRequestDto dto,
//                            @RequestPart(value = "clubImage", required = false) List<MultipartFile> clubImage){
    @PostMapping("/create")
    public ResultDTO create(ClubRequest.CreateClubRequestDto dto){
        try{

//            List<MultipartFile> clubImage = new ArrayList<>();

            Map<String, Object> result = clubService.create(dto);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 생성 완료.", result);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "모임 가입 ", description = "" +
            " 모임 가입." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 존재하지 않는 회원입니다." +
            "\n- NOT_FOUND_CLUB: 존재하지 않는 모임입니다." +
            "\n- MEMBERSHIP_LIMIT_EXCEEDED: 모임 가입 개수를 초과했습니다." +
            "\n- ALREADY_REGISTERED_MEMBER: 이미 가입된 회원입니다." +
            "\n- NOT_MATCHED_UNIVERSITY: 같은 대학이어야 합니다." +
            "\n- YOU_ARE_MASTER: 본인이 생성한 모임입니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/join")
    public ResultDTO join(@RequestBody ClubJoinRequest request){
        try{
            clubService.join(request);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 가입 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "모임 삭제 ", description = "" +
            " 모임 삭제." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long clubId, Long memberIdx){
        try{
            clubService.delete(clubId, memberIdx);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "모임 삭제 완료.", null);
        } catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "모임 수정 ", description = "" +
            " 모임 수정." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    //.
    @PatchMapping("/modify")
    public ResultDTO modifyClub(ClubRequest.ModifyClubRequestDto dto){
        try{
            clubService.update(dto);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "모임 수정 완료.", null);
        } catch(CustomException e){
            if(e.getCustomErrorCode().getStatusCode().equals("ACCESS_DENIED")) {
                return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(),"모임 관리자만 수정할 수 있어요.", null);
            } else {
                return ResultDTO.of(false, ApiResponseCode.INTERNAL_SERVER_ERROR.getCode(), ApiResponseCode.INTERNAL_SERVER_ERROR.getMessage(), null);
            }
        }
    }

    @GetMapping("/list")    /** 모임 리스트 조회 **/
    public Object list(Long memberIdx){
        try{
            List<ClubListDto> requestDtoList = clubService.list(memberIdx);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "모임 수정 완료.", requestDtoList);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), "모임 전체 리스트 조회 완료");
        }
    }

    @Operation(summary = "모임 정보 조회 ", description = "" +
            " 모임 정보 조회." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 해당 정보를 찾을 수 없습니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/info")
    public ResultDTO info(@RequestParam Long clubId){
        try {
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 정보 조회 완료.", clubService.info(clubId));
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "모임 탈퇴 ", description = "" +
            " 모임 탈퇴." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 해당 정보를 찾을 수 없습니다." +
            "\n- NOT_FOUND_CLUB: 존재하지 않는 모임입니다." +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/secession")
    public ResultDTO secession(@RequestBody ClubJoinRequest request){
        try{
            clubService.secession(request);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"모임 탈퇴 완료.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "모임 추천", description = "" +
            " 모임 추천" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 해당 정보를 찾을 수 없습니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/suggest")
    public ResultDTO<List<SuggestClub>> suggest(@RequestParam Long memberIdx , @RequestParam(required = false) String fcmToken){
        try {
            // fcmToken이 null이 아니고, 빈 문자열이 아닌 경우에만 실행
            if (fcmToken != null && !fcmToken.isEmpty()) {
                clubService.fcmToken(fcmToken, memberIdx);
            }

            List<SuggestClub> suggestClubList = clubService.suggest(memberIdx);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"조회 성공", suggestClubList);

        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "번개 모집", description = "" +
            " 번개 모집" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 해당 정보를 찾을 수 없습니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/mercenary")
    public ResultDTO<List<MercenaryDto>> mercenary(@RequestParam Long memberIdx){
        try {
            List<MercenaryDto> mercenaryDtos = clubService.mercenary(memberIdx);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),"조회 성공", mercenaryDtos);
        } catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }

    @Operation(summary = "모임 추방", description = "" +
            " 모임 멤버를 추방합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 해당 정보를 찾을 수 없습니다." +
            "\n- NOT_FOUND_CLUB: 존재하지 않는 모임입니다." +
            "\n- NOT_FOUND_USER: 가입되지 않은 회원입니다." +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @DeleteMapping("/expel")
    public ResultDTO expelMember(@RequestBody ExpelClub request){
        try{
            clubService.expelMember(request);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(),"모임에서 회원을 추방했습니다.", null);
        }catch(CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


}

