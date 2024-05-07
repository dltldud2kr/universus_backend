package com.example.gazamung.univBattle.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.MatchStatus;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.participant.entity.Participant;
import com.example.gazamung.participant.repository.ParticipantRepository;
import com.example.gazamung.univBattle.dto.*;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.repository.UnivBattleRepository;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.signature.qual.IdentifierOrPrimitiveType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnivBattleServiceImpl implements UnivBattleService {

    private final UniversityRepository universityRepository;
    private final UnivBattleRepository univBattleRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMemberRepository chatMemberRepository;
    // 스케줄러 생성
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // 예약 작업 관리를 위한 ConcurrentHashMap
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();




    /**
     * 대학 vs 대학 대항전 생성
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
                .matchStatus(MatchStatus.RECRUIT)
                .hostUnivName(university.getSchoolName())
                .cost(request.getCost())
                .regDt(LocalDateTime.now())
                .hostUnivLogo(university.getLogoImg())
                .build();

        UnivBattle result = univBattleRepository.save(univBattle);


        // 주최팀 학교 로고 업데이트
//        result.setHostUnivLogo(university.getLogoImg());
//        univBattleRepository.save(result);

        //대항전 참가자 테이블에 생성자 추가
        Participant participant = Participant.builder()
                .memberIdx(member.getMemberIdx())
                .univBattleId(result.getUnivBattleId())
                .userName(member.getName())
                .nickName(member.getNickname())
                .univId(univId)
                .build();
        participantRepository.save(participant);

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomType(0)
                .dynamicId(univBattle.getUnivBattleId())
                .build();

        chatRoomRepository.save(chatRoom);

        Optional<ChatMember> findChatMember = chatMemberRepository.findByMemberIdxAndChatRoomIdAndChatRoomType(member.getMemberIdx(),chatRoom.getChatRoomId(),0);
        if(findChatMember.isEmpty()){
            ChatMember chatMember = ChatMember.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .chatRoomType(0)
                    .memberIdx(member.getMemberIdx())
                    .build();

            chatMemberRepository.save(chatMember);
        }
        return true;

    }

    /**
     *  대항전 대표 참가
     * @param request
     * @return
     */
    @Override
    @Transactional
    public boolean GuestLeaderAttend(GuestLeaderAttendRequest request) {

        UnivBattle univBattle = univBattleRepository.findById(request.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        //이미 참가팀 대표가 있는지 확인
        if (univBattle.getGuestLeader() != null){
            throw new CustomException(CustomExceptionCode.REPRESENTATIVE_ALREADY_EXISTS);
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
        univBattle.setMatchStatus(MatchStatus.WAITING);

        // 참가팀 로고 업데이트
        Optional<University> findHostUniv = universityRepository.findById(guestUniv);
        univBattle.setGuestUnivLogo(findHostUniv.get().getLogoImg());

        // 초대코드 생성
        univBattle.setInvitationCode(generateRandomString(8));

        univBattleRepository.save(univBattle);

        // 참가인원 초과 여부 체크
        int totalParticipant = participantRepository.countByUnivBattleId(univBattle.getUnivBattleId());

        // 마지막 참가자일 경우 대기중으로 변경.
        if(totalParticipant == univBattle.getTeamPtcLimit() * 2 - 1){
            univBattle.setMatchStatus(MatchStatus.PREPARED);
        }

        Participant participant = Participant.builder()
                .memberIdx(guest.getMemberIdx())
                .nickName(guest.getNickname())
                .userName(guest.getName())
                .univBattleId(request.getUnivBattleId())
                .univId(guestUniv)
                .build();

        participantRepository.save(participant);

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(0,univBattle.getUnivBattleId());

        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .customChatRoomName(univBattle.getHostUnivName() + "대항전")
                .memberIdx(guest.getMemberIdx())
                .build();

        chatMemberRepository.save(chatMember);

        ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(univBattle.getHostLeader(),chatRoom.getChatRoomId());

        hostChatMember.setCustomChatRoomName(univBattle.getGuestUnivName() + "대항전");

        chatMemberRepository.save(hostChatMember);

        return true;

    }



    @Override
    @Transactional
    public boolean attend(AttendRequest request) {

        // 참가자 정보 조회
        Member member = memberRepository.findById(request.getMemberIdx())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 대항전 정보 조회
        UnivBattle univBattle = univBattleRepository.findById(request.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 이미 진행중인 경우 참가 불가
        if(univBattle.getMatchStatus() == MatchStatus.IN_PROGRESS || univBattle.getMatchStatus() == MatchStatus.COMPLETED){
            throw new CustomException(CustomExceptionCode.ALREADY_IN_PROGRESS);
        }

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

        // 마지막 참가자일 경우 대기중으로 변경.
        if(totalParticipant == univBattle.getTeamPtcLimit() * 2  - 1){
            univBattle.setMatchStatus(MatchStatus.PREPARED);
        }

        // 참가자 저장
        Participant participant = Participant.builder()
                .memberIdx(request.getMemberIdx())
                .univBattleId(request.getUnivBattleId())
                .univId(member.getUnivId())
                .userName(member.getName())
                .nickName(member.getNickname())
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
                return univBattleRepository.findByMatchStatus(MatchStatus.RECRUIT);
            case 2:
                return univBattleRepository.findByMatchStatus(MatchStatus.WAITING);
            case 3:
                return univBattleRepository.findByMatchStatus(MatchStatus.IN_PROGRESS);
            case 4:
                return univBattleRepository.findByMatchStatus(MatchStatus.COMPLETED);

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

        String guestUvName = "";

        University Hostuniversity = universityRepository.findById(univBattle.getHostUniv())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_UNIVERSITY));

        if(univBattle.getGuestUniv() != null) {
            University GuestUniversity = universityRepository.findById(univBattle.getGuestUniv())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_UNIVERSITY));
            guestUvName = GuestUniversity.getSchoolName();
        }



        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(0,univBattleId);
        long chatRoomId = chatRoom.getChatRoomId();


        String hostUvName = Hostuniversity.getSchoolName();


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
        response.put("chatRoomType", chatRoom.getChatRoomType());
        response.put("chatRoomId", chatRoomId);

        if(univBattle.getWinUniv() != null) {
            String winUniv = universityRepository.findById(univBattle.getWinUniv()).orElse(null).getSchoolName();
            response.put("winUnivName", winUniv);
        }


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


    @Override
    public boolean matchStart(Long univBattleId) {

        UnivBattle univBattle = univBattleRepository.findById(univBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 준비가 안 된 경우 경기 시작을 할 수 없음.
        if(univBattle.getMatchStatus()!= MatchStatus.PREPARED){
            throw new CustomException(CustomExceptionCode.CANNOT_START_MATCH);
        }
        // 경기 참여 인원 수와 경기 인원 수가 같을 경우에만 경기 시작.
        int ptcCount = participantRepository.countByUnivBattleId(univBattleId);
        if(ptcCount != univBattle.getTeamPtcLimit()){
            throw new CustomException(CustomExceptionCode.INSUFFICIENT_MATCH_PLAYERS);
        }

        univBattle.setMatchStatus(MatchStatus.IN_PROGRESS);
        univBattle.setMatchStartDt(LocalDateTime.now());

        univBattleRepository.save(univBattle);

        return true;
    }

    /**
     * 경기 결과 요청 (주최팀 -> 참가팀)
     * 경기의 결과 기록은 주최측 대표자가 함.
     * 그 기록은 참가팀 대표자에게 보내서 확인 받아야한다.
     * @param dto
     * @return
     */

    @Override
    public boolean matchResultReq(MatchResultRequest dto) {
        UnivBattle univBattle = univBattleRepository.findById(dto.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 경기 상태가 진행 중이 아닐 경우 예외처리
        if (univBattle.getMatchStatus() != MatchStatus.IN_PROGRESS) {
            throw new CustomException(CustomExceptionCode.NOT_IN_PROGRESS);
        }

        // 경기 점수와 승리한 대학교 기록 설정
        univBattle.setGuestScore(dto.getGuestScore());
        univBattle.setHostScore(dto.getHostScore());
        univBattle.setWinUniv(dto.getWinUniv());

        univBattleRepository.save(univBattle);

        // 1분 후에 실행될 스케줄링 작업을 생성합니다. (테스트용)
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
            log.info("Scheduled Task 실행");
            checkIncompleteMatch(univBattle.getUnivBattleId());
        }, 1, TimeUnit.MINUTES); // 1분 후에 실행

        // 예약된 작업을 관리 목록에 추가
        scheduledTasks.put(univBattle.getUnivBattleId(), scheduledFuture);

        return true;
    }

    /**
     * 경기 결과 응답 (참가팀)
     * 주최자의 경기기록에 대해 응답함.
     * < resultYN 값에 따른 결과 >
     * true: 경기 종료 (MatchStatus.COMPLETE)로 변경
     * false: 주최측에 경기결과 재요청 (MatchStatus.IN_PROGRESS)유지
     * 어떠한 결과값이든 스케줄 작업을 삭제 및 취소함.
     * @param dto
     * @return
     */
    @Override
    public boolean matchResultRes(MatchResultResponse dto) {
        UnivBattle univBattle = univBattleRepository.findById(dto.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 이미 경기가 완료된 상태일 경우 예외 처리
        if (univBattle.getMatchStatus() == MatchStatus.COMPLETED) {
            throw new CustomException(CustomExceptionCode.ALREADY_END_MATCH);
        }

        // 주최자측 결과 보고에 응답했는 경우 관리 목록에서 해당 경기의 스케줄링 작업을 가져와 취소
        ScheduledFuture<?> future = scheduledTasks.remove(univBattle.getUnivBattleId());
        if (future != null) {
            future.cancel(false); // 작업 취소
            log.info("Scheduled Task 취소 시도");
        }

        // true 로 반응한 경우 경기결과에 문제가 없으니 COMPLETED 처리
        if (dto.isResultYN()) {
            univBattle.setMatchStatus(MatchStatus.COMPLETED);
        }
        // false 로 반응한 경우 점수 및 승리팀 기록 초기화.
        else {
            univBattle.setGuestScore(null);
            univBattle.setHostScore(null);
            univBattle.setWinUniv(null);
        }

        univBattleRepository.save(univBattle);

        return true;
    }

    /**
     * 경기 종료 메서드
     * @author 이시영
     * @param univBattleId
     */
    // 경기 종료 처리 메서드
    private void checkIncompleteMatch(Long univBattleId) {
        UnivBattle univBattle = univBattleRepository.findById(univBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 경기 상태가 완료되지 않았을 경우 경기를 종료 처리합니다.
        if (univBattle.getMatchStatus() != MatchStatus.COMPLETED) {
            univBattle.setMatchStatus(MatchStatus.COMPLETED);
            univBattle.setEndDt(LocalDateTime.now());
            univBattleRepository.save(univBattle);
            log.info("경기 종료 처리 완료");
        }
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