package com.example.gazamung.club.service;

import com.example.gazamung.S3FileUploader.UploadImage;
import com.example.gazamung.S3FileUploader.UploadService;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.club.dto.*;
import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.repository.ClubRepository;
import com.example.gazamung.clubMember.entity.ClubMember;
import com.example.gazamung.clubMember.repository.ClubMemberRepository;
import com.example.gazamung.event.entity.Event;
import com.example.gazamung.event.repository.EventRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.mapper.ClubMapper;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.member.service.MemberServiceImpl;
import com.example.gazamung.univBoard.entity.UnivBoard;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final UploadService uploadService;
    private final ClubMemberRepository clubMemberRepository;
    private final MemberServiceImpl memberServiceImpl;
    private final ClubMapper clubMapper;
    private final EventRepository eventRepository;
    private final UnivBoardRepository univBoardRepository;

    /**
     * @param dto
     * @return
     * @title 모임 생성
     * @created 24.03.26 이승열
     */
    // @TODO    카테고리 값 엔티티 매핑 추가해야함.  현재 TEST
    public Map<String, Object> create(ClubRequest.CreateClubRequestDto dto) {

        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        Club club = Club.builder()
                .memberIdx(member.getMemberIdx())
                .univId((member.getUnivId()))
                .eventId(dto.getEventId())
                .clubName(dto.getClubName())
                .introduction(dto.getIntroduction())
                .price(dto.getPrice())
                .maximumMembers(dto.getMaximumMembers())
                .regDt(LocalDateTime.now())
                .build();

        //모임을 먼저 생성하는 이유는 생성된 IDX로 업로드 이미지를 맵핑해주기 위해 1차적으로 먼저 생성
        Club savedClub = clubRepository.save(club);

        List<Map<String, Object>> uploadedImages = null;

        if (dto.getClubImage() != null && !dto.getClubImage().isEmpty()) {
            //정상적으로 모임이 생성됐는 경우 리뷰에 등록할 이미지를 첨부했는지 확인하고 해당 리뷰 IDX에 이미지 업로드를 실행함
            uploadedImages = uploadService.upload(dto.getClubImage(), dto.getMemberIdx(), AttachmentType.CLUB, savedClub.getClubId());

            // 업로드된 이미지 중 0번째 이미지를 대표 이미지로 지정
            Long representIdx = null;
            if (!uploadedImages.isEmpty()) {
                representIdx = (Long) uploadedImages.get(0).get("idx");
                club.setRepresentIdx(representIdx);
                clubRepository.save(club); // 대표 이미지 설정 후 다시 저장
            }
        }

        // uploadImages 와 club 정보를 함께 반환
        Map<String, Object> result = new HashMap<>();
        result.put("uploadedImages", uploadedImages);
        result.put("club", club);

        return result;

    }

    /**
     * @param dto
     * @title 모임 수정
     * @created 24.03.13 이시영
     * @description 모임 수정요청시 기존 업로드 되었던 S3 업로드 정보를 모두 삭제합니다.
     * 수정요청에 새로 담겨있는 첨부파일로 새로 업로드하고 DB에 새로 맵핑합니다.
     */
    @Override
    @Transactional
    public void update(ClubRequest.ModifyClubRequestDto dto) {
        try {
            //수정 요청한 모임을 확인함
            Club createdClub = clubRepository.findById(dto.getClubId()).get();

            //수정을 요청한 사용자와 작성자가 다른 경우 : (본인인지의 대한 유효성 검사)
            if (dto.getMemberIdx() != createdClub.getMemberIdx()) {
                throw new CustomException(CustomExceptionCode.ACCESS_DENIED);
            }

            if (dto.getClubImage() != null && !dto.getClubImage().isEmpty()) {
                //유효성 검증에 모두 통과했다면 버킷에 업로드되어있는 CLUB 파일을 모두 삭제합니다.
                //해당 모임에 업로드 등록되어있는 이미지를 검색합니다.
                List<UploadImage> imageByAttachmentType = uploadService.getImageByAttachmentType(AttachmentType.CLUB, dto.getClubId());
                String[] removeTarget = new String[imageByAttachmentType.size() + 1];

                int removeCount = 0;

                //업로드된 이미지가 잇는 경우
                try {
                    if (imageByAttachmentType.size() > 0) {
                        for (UploadImage file : imageByAttachmentType) {
                            // 문자열에서 ".com/" 다음의 정보를 추출
                            int startIndex = file.getImageUrl().indexOf(".com/") + 5;
                            String result = file.getImageUrl().substring(startIndex);
                            removeTarget[removeCount] = result;
                            removeCount++;
                        }
                        //등록되어있는 파일 정보 삭제 요청.
                        uploadService.removeS3Files(removeTarget);
                        //데이터베이스에 맵핑되어있는 정보삭제
                        uploadService.removeDatabaseByReviewIdx(dto.getClubId());
                    }
                } catch (CustomException e) {
                    throw new CustomException(CustomExceptionCode.SERVER_ERROR);
                }

                //새롭게 요청온 업로드 이미지를  버킷에 업로드함.
                uploadService.upload(dto.getClubImage(), dto.getMemberIdx(), AttachmentType.CLUB, createdClub.getClubId());

                //업로드된 이미지 정보를 데이터베이스
                List<UploadImage> getRepresentIdx = uploadService.getImageByAttachmentType(AttachmentType.CLUB, createdClub.getClubId());

            }

            createdClub.setEventId(dto.getEventId());
            createdClub.setClubName(dto.getClubName());
            createdClub.setIntroduction(dto.getIntroduction());
            createdClub.setPrice(dto.getPrice());
            createdClub.setMaximumMembers(dto.getMaximumMembers());

            clubRepository.save(createdClub);
        } catch (CustomException e) {
            System.err.println("modifyJournal Exception : " + e);
        }

    }


    /**
     * @param clubId
     * @param memberIdx
     * @title 모임 삭제
     * @created 24.03.13 이시영
     * @description 모임 삭제요청시 기존 업로드 되었던 S3 업로드 정보를 모두 삭제합니다.
     */
    @Transactional
    public void delete(Long clubId, Long memberIdx) {

        try {
            Club club = clubRepository.findById(clubId)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));

            // 모임을 DB 에서 삭제.
            clubRepository.delete(club);

            //모임장과 삭제 요청 회원이 동일한지 확인.
            if (club.getMemberIdx().equals(memberIdx)) {

                //유효성 검증에 모두 통과했다면 버킷에 업로드되어있는 리뷰 파일을 모두 삭제.
                //해당 리뷰에 업로드 등록되어있는 이미지를 검색.
                List<UploadImage> imageByAttachmentType = uploadService.getImageByAttachmentType(AttachmentType.CLUB, clubId);
                String[] removeTarget = new String[imageByAttachmentType.size() + 1];

                int removeCount = 0;
                //업로드된 이미지가 잇는 경우
                try {
                    if (imageByAttachmentType.size() > 0) {
                        for (UploadImage file : imageByAttachmentType) {
                            // 문자열에서 ".com/" 다음의 정보를 추출
                            int startIndex = file.getImageUrl().indexOf(".com/") + 5;
                            String result = file.getImageUrl().substring(startIndex);
                            removeTarget[removeCount] = result;
                            removeCount++;
                        }
                        //등록되어있는 파일 정보 삭제 요청.
                        uploadService.removeS3Files(removeTarget);
                        //데이터베이스에 맵핑되어있는 정보삭제
                        uploadService.removeDatabaseByReviewIdx(clubId);
                    }
                } catch (CustomException e) {
                    throw new CustomException(CustomExceptionCode.SERVER_ERROR);
                }
            }
        } catch (CustomException e) {
            throw new CustomException(CustomExceptionCode.SERVER_ERROR);
        } catch (EmptyResultDataAccessException e) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND);
        }

    }


    /**
     * @param
     * @title 모임 리스트 조회
     * @created 24.03.27 이승열
     * @description Entity 객체를 Dto 로 변환하여 리턴합니다.
     */
    @Override
    public List<ClubDto> list() {
        List<Club> clubList = clubRepository.findAll();
        return convertToDto(clubList);
    }

    /**
     * @param clubId
     * @title 모임 정보 조회
     * @created 24.03.27 이승열
     * @description Entity 객체를 Dto 로 변환하여 리턴합니다.
     */
    public ClubDto info(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));

        // 클럽에 연결된 이미지 정보 조회
        List<UploadImage> clubImage = uploadService.getImageByAttachmentType(AttachmentType.CLUB, clubId);

        // 현재 멤버 수 계산 (모임장 포함)
        Long currentMembers = calculateCurrentMembers(club.getClubId());

        Member LeaderMember = memberRepository.findById(club.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        String LeaderNickname = LeaderMember.getNickname();
        String LeaderProfileImg = LeaderMember.getProfileImgUrl();


        // 클럽 정보를 Dto로 변환하여 반환
        return ClubDto.builder()
                .clubId(club.getClubId())
                .memberIdx(club.getMemberIdx())
                .eventId(club.getEventId())
                .univId(club.getUnivId())
                .clubName(club.getClubName())
                .introduction(club.getIntroduction())
                .regDt(club.getRegDt())
                .price(club.getPrice())
                .maximumMembers(club.getMaximumMembers())
                .currentMembers(currentMembers + 1) // 모임장 포함
                .clubImage(clubImage) // 클럽 이미지 정보 추가
                .LeaderNickname(LeaderNickname)
                .LeaderProfileImg(LeaderProfileImg)
                .build();
    }

    /**
     * @param request
     * @title 모임 탈퇴
     * @created 24.03.28 이승열
     */
    @Override
    public void secession(ClubJoinRequest request) {

        // 회원과 클럽 존재 여부 확인
        memberRepository.findById(request.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_CLUB));

        // 가입된 회원인지 확인 후 탈퇴
        Optional<ClubMember> clubMemberOptional = clubMemberRepository.findByClubIdAndMemberIdx(request.getClubId(), request.getMemberIdx());
        if (clubMemberOptional.isPresent()) {
            ClubMember clubMember = clubMemberOptional.get();
            clubMemberRepository.delete(clubMember);
        } else {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_USER);
        }
    }

    /**
     * @param memberIdx
     * @title 모임 추천
     * @created 24.05.06 이승열
     * @description 사용자의 관심 모임 위주로 랜덤한 5개 모임의 정보 반환
     */
    public List<SuggestClub> suggest(Long memberIdx) {

        // 내가 가입한 클럽 목록 검색
        List<ClubMember> clubMembers = clubMemberRepository.findByMemberIdx(memberIdx);

        // 멤버 ID를 사용하여 Club 엔티티 목록 검색 (내가 모임장인 경우)
        List<Club> clubList = clubRepository.findByMemberIdx(memberIdx);

        List<Long> clubIds = new ArrayList<>(); // 클럽 ID를 저장할 리스트
        List<Long> eventIds = new ArrayList<>(); // 이벤트 ID를 저장할 리스트

        // 클럽 멤버가 있는 경우, 해당 클럽의 ID와 이벤트 ID를 추출
        if (!clubMembers.isEmpty()) {
            clubIds.addAll(clubMembers.stream()
                    .map(ClubMember::getClubId)
                    .collect(Collectors.toList()));

            eventIds.addAll(clubRepository.findAllById(clubIds).stream()
                    .map(Club::getEventId)
                    .collect(Collectors.toList()));
        }

        // 모임장인 클럽이 있는 경우, 해당 클럽의 이벤트 ID를 추가하고 클럽 ID도 추출
        if (!clubList.isEmpty()) {
            eventIds.addAll(clubList.stream()
                    .map(Club::getEventId)
                    .collect(Collectors.toList()));

            clubIds.addAll(clubList.stream()
                    .map(Club::getClubId)
                    .collect(Collectors.toList()));
        }

        List<Club> clubs;
        // 클럽 멤버가 없고, 모임장인 클럽도 없는 경우 모든 클럽을 대상으로 추천
        if (clubIds.isEmpty() && eventIds.isEmpty()) {
            clubs = clubRepository.findAll();
        } else {
            // 추출된 이벤트 ID와 클럽 ID를 사용하여 클럽을 검색
            clubs = clubRepository.findAllByEventIdInAndClubIdNotIn(eventIds, clubIds);
        }

        // 랜덤하게 띄우기 위해 리스트에 담아 이후 shuffle
        List<SuggestClub> suggestedClubs = clubs.stream().map(club -> {

            Long currentMembers = clubMemberRepository.countByClubId(club.getClubId()); // 현재 클럽 멤버 수 조회
            List<UploadImage> clubImage = uploadService.getImageByAttachmentType(AttachmentType.CLUB, club.getClubId());

            String imageUrl;
            if (!clubImage.isEmpty()) {
                imageUrl = clubImage.get(0).getImageUrl();
            } else {
                // 클럽 이미지가 없는 경우
                imageUrl = "";
            }
            Optional<Event> event = eventRepository.findById(club.getEventId());
            if (event.isEmpty()) {
                throw new CustomException(CustomExceptionCode.NOT_FOUND_EVENT);
            }

            return SuggestClub.builder()
                    .clubId(club.getClubId())
                    .clubName(club.getClubName())
                    .eventName(event.get().getEventName())
                    .currentMembers(currentMembers + 1)
                    .imageUrl(imageUrl)
                    .build();
        }).collect(Collectors.toList());

        Collections.shuffle(suggestedClubs); // 리스트 섞기




        return suggestedClubs;
    }


    /**
     * @param request
     * @title 모임 가입
     * @created 24.05.02 이승열
     */
    @Override
    public void join(ClubJoinRequest request) {
        ClubMember clubMember = ClubMember.builder()
                .memberIdx(request.getMemberIdx())
                .clubId(request.getClubId())
                .build();
        clubMemberRepository.save(clubMember);
    }

    /**
     * @param memberIdx
     * @title 번개(용병) 모집
     * @created 24.05.07 이승열
     * @description 학교 커뮤니티 내 모집 게시판의 게시글 3개를 최신순으로 반환
     */
    @Override
    public List<MercenaryDto> mercenary(Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // categoryId가 1(모집)인 게시판의 게시물을 최신순으로 가져옴
        List<UnivBoard> univBoardList = univBoardRepository.findByCategoryIdAndUnivId(1L, member.getUnivId(), Sort.by(Sort.Direction.DESC, "regDt"));

        if (univBoardList.isEmpty()) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND_BOARD);
        }

        // univBoardList의 각각의 UnivBoard에 대해 이벤트 정보를 가져와서 MercenaryDto로 변환
        return univBoardList.stream()
                .map(univBoard -> {
                    Optional<Event> event = eventRepository.findById(univBoard.getEventId());
                    if (event.isEmpty()) {
                        throw new CustomException(CustomExceptionCode.NOT_FOUND_EVENT);
                    }
                    // 첫 번째 이미지만 반환
                    List<UploadImage> postImages = uploadService.getImageByAttachmentType(AttachmentType.POST, univBoard.getUnivBoardId());
                    String firstImageUrl = postImages.isEmpty() ? null : postImages.get(0).getImageUrl();

                    return MercenaryDto.builder()
                            .univBoardId(univBoard.getUnivBoardId())
                            .title(univBoard.getTitle())
                            .eventName(event.get().getEventName())
                            .lat(univBoard.getLat())
                            .lng(univBoard.getLng())
                            .place(univBoard.getPlace())
                            .imageUrl(firstImageUrl)
                            .matchDt(univBoard.getMatchDt())
                            .build();
                })
                .limit(3) // 3개만 반환
                .collect(Collectors.toList());
    }

    @Override
    public void fcmToken(String fcmToken, Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        member.setFcmToken(fcmToken);
        memberRepository.save(member);

    }


    private List<ClubDto> convertToDto(List<Club> clubList) {

        return clubList.stream()
                .map(club -> {
                    Long currentMembers = calculateCurrentMembers(club.getClubId()); // 현재 멤버 수 계산
                    List<UploadImage> clubImage = uploadService.getImageByAttachmentType(AttachmentType.CLUB, club.getClubId());

                    return ClubDto.builder()
                            .clubId(club.getClubId())
                            .memberIdx(club.getMemberIdx())
                            .eventId(club.getEventId())
                            .univId(club.getUnivId())
                            .clubName(club.getClubName())
                            .introduction(club.getIntroduction())
                            .regDt(club.getRegDt())
                            .price(club.getPrice())
                            .maximumMembers(club.getMaximumMembers())
                            .currentMembers(currentMembers + 1) // 모임장 포함
                            .clubImage(clubImage) // 클럽 이미지 정보 추가
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 현재 멤버 수 계산 메서드
    private Long calculateCurrentMembers(Long clubId) {
        return clubMemberRepository.countByClubId(clubId);

    }
}

