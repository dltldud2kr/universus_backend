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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<Long, CompletableFuture<Void>> incompleteMatchThreads = new ConcurrentHashMap<>();



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
                .matchStatus(MatchStatus.RECRUIT)
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
                .userName(member.getName())
                .nickName(member.getNickname())
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

        Optional<ChatMember> findChatMember = chatMemberRepository.findByMemberIdxAndChatRoomIdAndChatRoomType(member.getMemberIdx(),chatRoom.getChatRoomId(),0);
        if(findChatMember.isEmpty()){
            ChatMember chatMember = ChatMember.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .chatRoomType(0)
                    .memberIdx(member.getMemberIdx())
                    .chatRoomName(chatRoom.getChatRoomName())
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
        univBattle.setMatchStatus(MatchStatus.WAITING);

        // 참가팀 로고 업데이트
        Optional<University> findHostUniv = universityRepository.findById(guestUniv);
        univBattle.setGuestUnivLogo(findHostUniv.get().getLogoImg());

        // 초대코드 생성
        univBattle.setInvitationCode(generateRandomString(8));

        univBattleRepository.save(univBattle);

        Participant participant = Participant.builder()
                .memberIdx(guest.getMemberIdx())
                .nickName(guest.getNickname())
                .userName(guest.getName())
                .univBattleId(request.getUnivBattleId())
                .univId(guestUniv)
                .build();

        participantRepository.save(participant);

        return true;

    }


    //@TODO 참가자가 꽉 찼을 때는 대기중으로 변경해야함.

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

    //@TODO 점수와 맞는 경기결과값이여야할거같음. 예외처리 추가할것.
    @Override
    public boolean matchResultReq(MatchResultRequest dto) {


        UnivBattle univBattle = univBattleRepository.findById(dto.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 진행중인 경기가 아닌 경우 예외처리
        if(univBattle.getMatchStatus() != MatchStatus.IN_PROGRESS){
            throw new CustomException(CustomExceptionCode.NOT_IN_PROGRESS);
        }

                univBattle.builder()
                .guestScore(dto.getGuestScore())
                .hostScore(dto.getHostScore())
                .winUniv(dto.getWinUniv())
                .build();

        univBattleRepository.save(univBattle);

        // 결과 전송 후 시간이 지나면 자동으로 결과 변경
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
//                Thread.sleep(Duration.ofHours(1).toMillis());

                // 1시간 대기. (테스트용으로 1분으로 변경.)
                Thread.sleep(Duration.ofMinutes(1).toMillis());

                // 1시간 후 아래 메서드 실행
                checkIncompleteMatch(univBattle.getUnivBattleId());
            } catch (InterruptedException e) {
                // 스레드 종료시 안전 종료ㅗ.
                Thread.currentThread().interrupt();
            }
        });

        // 쓰레드를 incompleteMatchThreads에 저장하여 관리
        incompleteMatchThreads.put(univBattle.getUnivBattleId(), future);

        return true;
    }


    @Override
    public boolean matchResultRes(MatchResultResponse dto) {

        UnivBattle univBattle = univBattleRepository.findById(dto.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 경기결과에 대해 정해진 기간 내에 응답이 없었을 때 경기는 주최측이 정한 경기기록으로 종료되어있음.
        // 이 때 참가측은 경기결과에 권한이 없음.
        if(univBattle.getMatchStatus() == MatchStatus.COMPLETED){
            throw new CustomException(CustomExceptionCode.ALREADY_END_MATCH);
        }

        if(dto.isResultYN()){
            univBattle.setMatchStatus(MatchStatus.COMPLETED);

            // remove 메서드는 CompletableFuture 안의 해당 값을 삭제하지만 future 쪽에 값을 반환할 수 있다.
            // CompletableFuture 에는 더이상 해당 쓰레드가 들어있지않고. 해당 쓰레드는 future.cancel 로 종료시킴.
            CompletableFuture<Void> future = incompleteMatchThreads.remove(univBattle.getUnivBattleId());
            if (future != null) {
                // 해당 쓰레드 종료
                future.cancel(true);
            }

        } else {
            univBattle.builder()
                    .guestScore(null)
                    .hostScore(null)
                    .winUniv(null)
                    .endDt(LocalDateTime.now())
                    .build();
        }

        univBattleRepository.save(univBattle);

        return true;
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

    /**
     * 경기 종료 메서드
     * @author 이시영
     * @param univBattleId
     */
    // 경기 종료 처리 메서드
    private void checkIncompleteMatch(Long univBattleId) {
        UnivBattle univBattle = univBattleRepository.findById(univBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        if (univBattle.getMatchStatus() != MatchStatus.COMPLETED) {
            // 경기 종료 상태로 변경
            univBattle.setMatchStatus(MatchStatus.COMPLETED);
            univBattle.setEndDt(LocalDateTime.now());
            univBattleRepository.save(univBattle);
        }
    }
}