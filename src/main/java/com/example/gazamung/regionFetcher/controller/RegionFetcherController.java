package com.example.gazamung.regionFetcher.controller;


import com.example.gazamung.regionFetcher.dto.AccessTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "행정구역 API", description = "")
public class RegionFetcherController {

    @Operation(summary = "행정구역 api 요청", description = "" +
            "## 행정구역 api 조회" +
            "\n### cd값에 따른 조회 결과" +
            "\n- 미입력 : 시,도 데이터 반환" +
            "\n- 입력 : 구 데이터 반환" +
            "\n ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "api 접근 성공"),
    })


    @GetMapping("/getAccessToken")
    public ApiResponse getData(@RequestParam(required = false) String cd) {
        // API 요청을 보낼 URL
        String apiUrl = "https://sgisapi.kostat.go.kr/OpenAPI3/addr/stage.json";

        // accessToken 획득
        String accessToken = getAccessToken();

        // API 요청을 보낼 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // 매개변수 설정
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("accessToken", accessToken);

        // cd 값이 주어진 경우 매개변수로 추가
        // cd를 안 받을 수도 있고. 받더라도 null값을 대비.
        if (cd != null && !cd.isEmpty()) {
            builder.queryParam("cd", cd);
        }

        // API 요청 보내고 응답 받기 (ApiResponse 객체로 응답 받음)
        ApiResponse response = restTemplate.getForObject(builder.toUriString(), ApiResponse.class);

        return response;
    }


    // accessToken 획득 메서드
    private String getAccessToken() {
        // API 요청을 보낼 URL
        String authUrl = "https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json";

        // HTTP 요청을 보낼 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // 매개변수 설정
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(authUrl)
                .queryParam("consumer_key", "4abd0813aed849d4817f")
                .queryParam("consumer_secret", "3f616f4e15ee4bd58b5c");

        // API 요청 보내고 응답 받기 (AccessTokenResponse 객체로 응답 받음)
        AccessTokenResponse response = restTemplate.getForObject(builder.toUriString(), AccessTokenResponse.class);

        // 응답에서 accessToken 추출
        String accessToken = response.getResult().getAccessToken();

        return accessToken;
    }

}
