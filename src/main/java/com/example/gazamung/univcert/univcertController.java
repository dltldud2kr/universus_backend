package com.example.gazamung.univcert;


import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.univcert.dto.CertificationCodeRequest;
import com.example.gazamung.univcert.dto.CertificationRequest;
import com.example.gazamung.university.University;
import com.example.gazamung.university.UniversityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/univAuth")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "학교인증 API", description = "")
public class univcertController {

    private final UniversityRepository universityRepository;


    private static final String API_KEY = "9fb7bab5-fda8-4147-a35a-3bec0dbfb5e0";
    private static final String CERTIFY_ENDPOINT = "https://univcert.com/api/v1/certify";
    private static final String CERTIFY_CODE_ENDPOINT = "https://univcert.com/api/v1/certifycode";

    private final RestTemplate restTemplate;

    @PostMapping("/certify")
    public ResponseEntity<String> certify(@RequestBody CertificationRequest request) {
        request.setKey(API_KEY);
        request.setUniv_check(true); // 기본값 설정

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CertificationRequest> httpRequest = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(CERTIFY_ENDPOINT, HttpMethod.POST, httpRequest, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping("/certifyCode")
    public Map<String, Object> certifyCode(@RequestBody CertificationCodeRequest request) throws JsonProcessingException {
        request.setKey(API_KEY);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CertificationCodeRequest> httpRequest = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(CERTIFY_CODE_ENDPOINT, HttpMethod.POST, httpRequest, String.class);


        // JSON 문자열을 ObjectMapper를 사용하여 파싱합니다.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        // success와 univName 필드의 값을 가져와서 출력합니다.
        boolean success = jsonNode.get("success").asBoolean();


        // success가 true일 때만 univName 필드의 값을 가져와서 처리합니다.
        if (success) {
            String schoolName = jsonNode.get("univName").asText();
            String email = jsonNode.get("certified_email").asText();
            System.out.println("University Name: " + schoolName);

            // 학교 이름으로 대학을 찾습니다.
            Optional<University> optionalUniversity = universityRepository.findBySchoolName(schoolName);

            // Optional에서 값을 가져올 때, 값이 있는지 확인하고 가져옵니다.
            if (optionalUniversity.isPresent()) {
                University university = optionalUniversity.get();
                Long univId = university.getId();

                Map<String, Object> result = new HashMap<>();
                result.put("univId", univId);
                result.put("email", email);
                result.put("success", success);

                return result;
            } else {
                // 대학을 찾지 못한 경우 예외 처리
                throw new CustomException(CustomExceptionCode.SERVER_ERROR);
            }
        } else {
            // 성공하지 않은 경우에 대한 처리
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            return result;
        }


    }
}



