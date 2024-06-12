package com.example.gazamung.fcmSend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    @Override
    public int sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
        try {
            String message = makeMessage(fcmSendDto);
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + getAccessToken());

            HttpEntity<String> entity = new HttpEntity<>(message, headers);

            String API_URL = "https://fcm.googleapis.com/v1/projects/universealert-1345f/messages:send";
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            log.info("FCM 서버 응답: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                return 1;
            } else {
                log.error("FCM 메시지 전송 실패: {}", response.getBody());
                return 0;
            }
        } catch (RestClientException e) {
            log.error("RestClientException: {}", e.getMessage(), e);
            return 0;
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            return 0;
        }
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/universealert-1345f-firebase-adminsdk-j2uur-902548ff11.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(fcmSendDto.getToken())
                        .notification(FcmMessageDto.Notification.builder()
                                .title(fcmSendDto.getTitle())
                                .body(fcmSendDto.getBody())
                                .image(null)
                                .build()
                        )
                        .data(Map.of(
                                "target", fcmSendDto.getTarget(),
                                "data", fcmSendDto.getData()
                        ))
                        .build())
                .validateOnly(false)
                .build();

        return om.writeValueAsString(fcmMessageDto);
    }
}
