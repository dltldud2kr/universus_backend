package com.example.gazamung.univcert;


import com.example.gazamung.univcert.dto.CertificationCodeRequest;
import com.example.gazamung.univcert.dto.CertificationRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/univAuth")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "학교인증 API", description = "")
public class univcertController {

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
    public ResponseEntity<String> certifyCode(@RequestBody CertificationCodeRequest request) {
        request.setKey(API_KEY);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CertificationCodeRequest> httpRequest = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(CERTIFY_CODE_ENDPOINT, HttpMethod.POST, httpRequest, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }




    }



