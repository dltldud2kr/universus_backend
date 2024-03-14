package com.example.gazamung.meeting;

import com.example.gazamung.S3FileUploader.UploadService;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.club.repository.ClubRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.meeting.dto.MeetingRequest;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final ClubRepository clubRepository;
    private final UploadService uploadService;

    /**
     * @title 정모생성
     * @created 24.03.13 이시영
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> create(MeetingRequest.CreateMeetingRequestDto request) {


        Member member = memberRepository.findById(request.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));


        Meeting meeting = Meeting.builder()
                .memberIdx(member.getMemberIdx())
                .clubId(request.getClubId())
                .content(request.getContent())
                .location(request.getLocation())
                .cost(request.getCost())
                .schedule(request.getSchedule())
                .regDt(LocalDateTime.now())
                .endDt(request.getEndDt())
                .maxParticipants(request.getMaximumParticipants())
                .build();

        Meeting savedMeeting = meetingRepository.save(meeting);

        //정상적으로 모임이 생성됐는 경우 리뷰에 등록할 이미지를 첨부했는지 확인하고 해당 리뷰 IDX에 이미지 업로드를 실행함
        List<Map<String, Object>> uploadedImages = uploadService.upload(request.getMeetingImages(), request.getMemberIdx(),
                AttachmentType.MEETING, savedMeeting.getMeetingId());

        Long representIdx = null;
        if (!uploadedImages.isEmpty()) {
            representIdx = (Long) uploadedImages.get(0).get("idx");
            meeting.setRepresentImgIdx(representIdx);
            meetingRepository.save(meeting); // 대표 이미지 설정 후 다시 저장
        }

        // uploadImages 와 club 정보를 함께 반환
        Map<String, Object> result = new HashMap<>();
        result.put("uploadedImages", uploadedImages);
        result.put("meeting", meeting);

        return result;
    }





}
