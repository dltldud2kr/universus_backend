package com.example.gazamung.club.service;

import com.example.gazamung.S3.UploadRepository;
import com.example.gazamung.S3.UploadService;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.club.dto.ClubDto;
import com.example.gazamung.club.dto.ClubRequest;
import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.repository.ClubRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final UploadService uploadService;
    private final UploadRepository uploadRepository;

    // @TODO    카테고리 값 엔티티 매핑 추가해야함.  현재 TEST
    public Map<String, Object>  create(ClubRequest.CreateClubRequestDto dto)   {

        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        Club club = Club.builder()
                .memberIdx(member.getMemberIdx())
                .clubName(dto.getClubName())
                .content(dto.getContent())
                .location(dto.getLocation())
                .categoryId(dto.getCategoryId())
                .maximumParticipants(dto.getMaximumParticipants())
                .ageStartLimit(dto.getAgeStartLimit())
                .ageEndLimit(dto.getAgeEndLimit())
                .bookmarkCnt(0L)
                .regDt(LocalDateTime.now())
                .build();

        Club savedClub = clubRepository.save(club);

        //정상적으로 리뷰가 생성됐는 경우 리뷰에 등록할 이미지를 첨부했는지 확인하고 해당 리뷰 IDX에 이미지 업로드를 실행함
        List<Map<String, Object>> uploadedImages = uploadService.upload(dto.getClubImage(), dto.getMemberIdx(), AttachmentType.CLUB, savedClub.getClubId());


        // 업로드된 이미지 중 0번째 이미지를 대표 이미지로 지정

        Long representIdx = null;
        if (!uploadedImages.isEmpty()) {
            representIdx = (Long) uploadedImages.get(0).get("idx");
            club.setRepresentIdx(representIdx);
            clubRepository.save(club); // 대표 이미지 설정 후 다시 저장
        }


        // uploadImages와 club 정보를 함께 반환
        Map<String, Object> result = new HashMap<>();
        result.put("uploadedImages", uploadedImages);
        result.put("club", club);

        return result;

    }

    public boolean delete(Long clubId, Long memberIdx) {
        Optional<Club> clubOpt = clubRepository.findById(clubId);

        if (clubOpt.isPresent()) {
            Club club = clubOpt.get();

            //모임장과 삭제 요청 회원이 동일한지 확인.
            if (club.getMemberIdx().equals(memberIdx)) {
                clubRepository.delete(club);
                return true;
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        }
        throw new CustomException(CustomExceptionCode.NOT_FOUND);

    }

    @Override
    public List<ClubDto> list() {
        List<Club> clubList = clubRepository.findAll();
        return convertToDto(clubList);
    }

    public ClubDto info(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND));
        return ClubDto.builder()
                .clubId(club.getClubId())
                .memberIdx(club.getMemberIdx())
                .clubName(club.getClubName())
                .content(club.getContent())
                .location(club.getLocation())
                .categoryId(club.getCategoryId())
                .regDt(club.getRegDt())
                .maximumParticipants(club.getMaximumParticipants())
                .currentParticipants(club.getCurrentParticipants())
                .ageStartLimit(club.getAgeStartLimit())
                .ageEndLimit(club.getAgeEndLimit())
                .bookmarkCnt(club.getBookmarkCnt())
                .build();
    }

    private List<ClubDto> convertToDto(List<Club> clubList) {
        return clubList.stream()
                .map(club -> ClubDto.builder()
                        .clubId(club.getClubId())
                        .memberIdx(club.getMemberIdx())
                        .clubName(club.getClubName())
                        .content(club.getContent())
                        .location(club.getLocation())
                        .categoryId(club.getCategoryId())
                        .regDt(club.getRegDt())
                        .maximumParticipants(club.getMaximumParticipants())
                        .currentParticipants(club.getCurrentParticipants())
                        .ageStartLimit(club.getAgeStartLimit())
                        .ageEndLimit(club.getAgeEndLimit())
                        .bookmarkCnt(club.getBookmarkCnt())
                        .build())
                .collect(Collectors.toList());
    }

}

