package com.example.gazamung.univBattle.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.Status;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.participant.entity.Participant;
import com.example.gazamung.participant.repository.ParticipantRepository;
import com.example.gazamung.univBattle.dto.AttendRequest;
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.repository.UnivBattleRepository;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnivBattleServiceImpl implements UnivBattleService {

    private final UniversityRepository universityRepository;
    private final UnivBattleRepository univBattleRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;


    /**
     * 대항전 생성
     * @param request
     * @return
     */
    @Override
    @Transactional
    public boolean create(UnivBattleCreateRequest request) {

        Member member = memberRepository.findById(request.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));


        log.info(member.getUsername());

        long univId = member.getUnivId();

        University university = universityRepository.findById(univId)
                .orElseThrow(() ->new CustomException(CustomExceptionCode.NOT_FOUND));

        // 대항전 생성
        UnivBattle univBattle = UnivBattle.builder()
                .hostLeader(request.getHostLeader())
                .eventId(request.getEventId())
                .hostUniv(univId)
                .battleDate(request.getBattleDate())
                .lat(request.getLat())
                .lng(request.getLng())
                .place(request.getPlace())
                .content(request.getContent())
                .teamPtcLimit(request.getTeamPtcLimit())
                .status(Status.RECRUIT)
                .hostUnivName(university.getSchoolName())
                .cost(request.getCost())
                .regDt(LocalDateTime.now())
                .build();

        UnivBattle result = univBattleRepository.save(univBattle);




        // 주최팀 학교 로고 업데이트
        result.setHostUnivLogo(university.getLogoImg());
        univBattleRepository.save(result);

        //대항전 참가자 테이블에 생성자 추가
        Participant participant = Participant.builder()
                .memberIdx(member.getMemberIdx())
                .univBattleId(result.getUnivBattleId())
                .userName(member.getNickname())
                .univId(univId)
                .build();
        participantRepository.save(participant);

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomType(0)
                .dynamicId(univBattle.getUnivBattleId())
                .chatRoomName(university.getSchoolName() + "대항전")
                .build();

        chatRoomRepository.save(chatRoom);
        return true;

    }

    /**
     *  대항전 대표 참가
     * @param request
     * @return
     */
    @Override
    public boolean GuestLeaderAttend(GuestLeaderAttendRequest request) {

        UnivBattle univBattle = univBattleRepository.findById(request.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        //이미 참가팀 대표가 있는지 확인
        if (univBattle.getGuestLeader() != null){
            throw new CustomException(CustomExceptionCode.SERVER_ERROR);
        }

        // 참가자 정보 조회
        Member guest = memberRepository.findById(request.getGuestLeader())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 주최자 대학교
        long hostUniv = univBattle.getHostUniv();
        // 참가자 대학교
        long guestUniv = guest.getUnivId();

        Optional<University> university = universityRepository.findById(guestUniv);
        String guestUnivName = university.get().getSchoolName();


        // 같은 학교는 참가 불가능.
        if (hostUniv == guestUniv) {
            throw new CustomException(CustomExceptionCode.SAME_UNIVERSITY);
        }

        // Guest 팀 정보 업데이트
        univBattle.setGuestLeader(request.getGuestLeader());
        univBattle.setGuestUniv(guestUniv);
        univBattle.setGuestUnivName(guestUnivName);

        // 상태를 "대기중" 으로 바꿈
        univBattle.setStatus(Status.WAITING);

        // 참가팀 로고 업데이트
        Optional<University> findHostUniv = universityRepository.findById(guestUniv);
        univBattle.setGuestUnivLogo(findHostUniv.get().getLogoImg());

        // 초대코드 생성
        univBattle.setInvitationCode(generateRandomString(8));

        univBattleRepository.save(univBattle);

        Participant participant = Participant.builder()
                .memberIdx(guest.getMemberIdx())
                .userName(guest.getUsername())
                .univBattleId(request.getUnivBattleId())
                .univId(guestUniv)
                .build();

        participantRepository.save(participant);

        return true;

    }

    @Override
    public boolean attend(AttendRequest request) {

        // 참가자 정보 조회
        Member member = memberRepository.findById(request.getMemberIdx())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 대항전 정보 조회
        UnivBattle univBattle = univBattleRepository.findById(request.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 이미 참가한 경우 예외 처리
        boolean alreadyAttended = participantRepository.existsByMemberIdxAndUnivBattleId(member.getMemberIdx(), univBattle.getUnivBattleId());
        if (alreadyAttended) {
            throw new CustomException(CustomExceptionCode.ALREADY_ATTENDED);
        }

        // 참가인원 초과 여부 체크
        int totalParticipant = participantRepository.countByUnivBattleId(univBattle.getUnivBattleId());
        if (totalParticipant >= univBattle.getTeamPtcLimit() * 2) {
            throw new CustomException(CustomExceptionCode.EXCEEDED_TOTAL_CAPACITY);
        }

        // 대학별 인원 초과 여부 체크
        int univTotalParticipant = participantRepository.countByUnivBattleIdAndUnivId(univBattle.getUnivBattleId(), member.getUnivId());
        if (univTotalParticipant >= univBattle.getTeamPtcLimit()) {
            throw new CustomException(CustomExceptionCode.EXCEEDED_UNIV_CAPACITY);
        }

        // 참가 코드 체크
        if (!request.getInvitationCode().equals(univBattle.getInvitationCode())){
            throw new CustomException(CustomExceptionCode.INVALID_INVITE_CODE);
        }

        // 참가자 저장
        Participant participant = Participant.builder()
                .memberIdx(request.getMemberIdx())
                .univBattleId(request.getUnivBattleId())
                .univId(member.getUnivId())
                .userName(member.getUsername())
                .build();
        participantRepository.save(participant);

        return true;
    }

    @Override
    public List<UnivBattle> list(int status) {

        Map<String, Object> response = new HashMap<>();


        switch (status) {
            case 0:
                return univBattleRepository.findAll();
            case 1:
                return univBattleRepository.findByStatus(Status.RECRUIT);
            case 2:
                return univBattleRepository.findByStatus(Status.WAITING);
            case 3:
                return univBattleRepository.findByStatus(Status.IN_PROGRESS);
            case 4:
                return univBattleRepository.findByStatus(Status.COMPLETED);

            default:
                throw new CustomException(CustomExceptionCode.SERVER_ERROR);
        }

    }

    @Override
    public Map<String, Object> info(long univBattleId) {

        UnivBattle univBattle = univBattleRepository.findById(univBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));


        int hostPtc = participantRepository.countByUnivBattleIdAndUnivId(univBattleId,univBattle.getHostUniv());
        int guestPtc = participantRepository.countByUnivBattleIdAndUnivId(univBattleId,univBattle.getGuestUniv());

        List<Participant> HostparticipantList = participantRepository.findAllByUnivIdAndUnivBattleId(univBattle.getHostUniv(),univBattleId);
        List<Participant> GuestparticipantList = participantRepository.findAllByUnivIdAndUnivBattleId(univBattle.getGuestUniv(),univBattleId);


        University Hostuniversity = universityRepository.findById(univBattle.getHostUniv())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_UNIVERSITY));
        University GuestUniversity = universityRepository.findById(univBattle.getGuestUniv())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_UNIVERSITY));


        String hostUvName = Hostuniversity.getSchoolName();
        String guestUvName = GuestUniversity.getSchoolName();

        // 응답용 Map 생성 및 값 추가
        Map<String, Object> response = new HashMap<>();


        Map<String, Object> hostTeam = new HashMap<>();
        hostTeam.put("hostUvName", hostUvName);
        hostTeam.put("hostPtcCnt", hostPtc);                  // 주최팀 회원 수
        hostTeam.put("hostPtcList", HostparticipantList);       // 주최팀 회원 리스트

        Map<String, Object> guestTeam = new HashMap<>();
        guestTeam.put("guestUvName", guestUvName);
        guestTeam.put("guestPtcCnt", guestPtc);               // 참가팀 회원 수
        guestTeam.put("guestPtcList", GuestparticipantList);    // 참가팀 회원 리스트

        response.put("HostTeam", hostTeam);
        response.put("GuestTeam", guestTeam);
        response.put("univBattle", univBattle);

//       myBatis 보류
//
//        // 응답용 Map 생성 및 값 추가
//        Map<String, Object> response = new HashMap<>();
//        response.put("univBattle", univBattle);
//        response.put("HostparticipantList", HostparticipantList);
//        response.put("GuestparticipantList", GuestparticipantList);
//        response.put("hostParticipantCount", hostPtc);
//        response.put("guestParticipantCount", guestPtc);


        return response;
    }


    /**
     * 초대 코드 생성기
     * @author 이시영
     * @param length
     * @return
     */
    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes).substring(0, length);
    }
}
