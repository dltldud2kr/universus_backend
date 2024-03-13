package com.example.gazamung.meeting;

import com.example.gazamung.meeting.dto.MeetingRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MeetingService {

    /**
     * 정모 생성
     * @param request
     * @return
     */
    Map<String, Object> create(MeetingRequest.CreateMeetingRequestDto request);
}
