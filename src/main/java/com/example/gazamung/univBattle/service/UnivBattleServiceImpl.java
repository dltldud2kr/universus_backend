package com.example.gazamung.univBattle.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.MatchStatus;
import com.example.gazamung._enum.MsgType;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.fcmSend.FcmSendDto;
import com.example.gazamung.fcmSend.FcmService;
import com.example.gazamung.mapper.RankMapper;
import com.example.gazamung.mapper.UnivBattleMapper;
import com.example.gazamung.mapper.UniversityMapper;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.service.NotificationService;
import com.example.gazamung.participant.entity.Participant;
import com.example.gazamung.participant.repository.ParticipantRepository;
import com.example.gazamung.univBattle.dto.*;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.repository.UnivBattleRepository;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
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
    private final FcmService fcmService;
    private final NotificationService notificationService;
    private final UnivBattleMapper univBattleMapper;
    private final UniversityMapper universityMapper;
    private final RankMapper rankMapper;
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
        /**
         * 1. 회원 검증
         * 2. 대학 정보 조회
         * 3. 대항전 생성
         * 4. Participant 테이블에 대항전 참가자 추가
         * 5. 채팅방 생성
         * 6. ChatMember 테이블에 채팅방 참가자 추가
         */

        // 회원 검증
        Member member = memberRepository.findById(request.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 회원 대학 정보 조회
        University university = universityRepository.findById(member.getUnivId())
                .orElseThrow(() ->new CustomException(CustomExceptionCode.NOT_FOUND));

        // 대항전 생성
        UnivBattle univBattle = UnivBattle.builder()
                .hostLeader(request.getHostLeader())
                .eventId(request.getEventId())
                .hostUniv(member.getUnivId())
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

        // 대항전 참가자 테이블에 생성자 추가
        Participant participant = Participant.builder()
                .memberIdx(member.getMemberIdx())
                .univBattleId(result.getUnivBattleId())
                .userName(member.getName())
                .nickName(member.getNickname())
                .univId(member.getUnivId())
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

        // 대항전 정보 조회
        UnivBattle univBattle = univBattleRepository.findById(request.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 이미 참가팀 대표가 있는지 확인
        if (univBattle.getGuestLeader() != null){
            throw new CustomException(CustomExceptionCode.REPRESENTATIVE_ALREADY_EXISTS);
        }

        // 참가자 정보 조회
        Member guest = memberRepository.findById(request.getGuestLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
        Member host = memberRepository.findById(univBattle.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        //회원 대학 정보 조회
        Optional<University> university = universityRepository.findById(guest.getUnivId());
        String guestUnivName = university.get().getSchoolName();

        // 주최자와 같은 학교는 참가 불가능.
        if (Objects.equals(univBattle.getHostUniv(), guest.getUnivId())) {
            throw new CustomException(CustomExceptionCode.SAME_UNIVERSITY);
        }

        // 해당 대항전 데이터에 Guest 팀 정보 업데이트
        univBattle.setGuestLeader(request.getGuestLeader());
        univBattle.setGuestUniv(guest.getUnivId());
        univBattle.setGuestUnivName(guestUnivName);
        univBattle.setGuestUnivLogo(university.get().getLogoImg());

        // 상태를 "대기중" 으로 바꿈
        univBattle.setMatchStatus(MatchStatus.WAITING);

        // 초대코드 생성
        univBattle.setInvitationCode(generateRandomString(8));

        // 업데이트 후 저장
        univBattleRepository.save(univBattle);

        // 참가인원 초과 여부 체크
        int totalParticipant = participantRepository.countByUnivBattleId(univBattle.getUnivBattleId());

        // 마지막 참가자일 경우 대기중으로 변경.
        if(totalParticipant == univBattle.getTeamPtcLimit() * 2 - 1){
            univBattle.setMatchStatus(MatchStatus.PREPARED);
        }


        /**
         * Participant 테이블에 대항전 참가자 추가
         * ChatMember 테이블에 채팅방 참가자 추가
         */
        Participant participant = Participant.builder()
                .memberIdx(guest.getMemberIdx())
                .nickName(guest.getNickname())
                .userName(guest.getName())
                .univBattleId(request.getUnivBattleId())
                .univId(guest.getUnivId())
                .build();

        participantRepository.save(participant);

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(0,univBattle.getUnivBattleId());

        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .customChatRoomName(univBattle.getHostUnivName() + "대항전")
                .memberIdx(guest.getMemberIdx())
                .chatRoomImg(host.getUnivLogoImg())
                .build();

        chatMemberRepository.save(chatMember);

        ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(univBattle.getHostLeader(),chatRoom.getChatRoomId());

        hostChatMember.setCustomChatRoomName(univBattle.getGuestUnivName() + "대항전");
        hostChatMember.setChatRoomImg(guest.getUnivLogoImg());

        chatMemberRepository.save(hostChatMember);

        // FCM 알림 전송 메서드 (주최자에게만 발송)
        FcmSendDto fcmSendDto = FcmSendDto.builder()
                .token("dWVpAXGoS0-qW8txlowMKt:APA91bEUdfKJYNQYLTDppQVhwQtXoUfwhgYLnTEgoLhZmTXfY8YbK" +
                        "HeAhiTDoMxXHChr2mhb-eA3eNb0MPUpAHHwceXciW4FZhck-AfWSbHQmwkTHRljIuTFZAhhDYDRKqF2WIZMnpYL")
                .title(guestUnivName + "대표자가 대항전에 참가했습니다.")
                .body(univBattle.getHostUnivName() + "vs" + univBattle.getGuestUnivName() + "대항전이 매칭되었습니다.")
                .build();
        try {
            fcmService.sendMessageTo(fcmSendDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 알림 전송 메서드 (주최자에게만 발송)
        NotifyCreateReq dto = NotifyCreateReq.builder()
                .type(MsgType.UNIV_BATTLE)
                .isRead(false)
                .receiver(univBattle.getHostLeader())
                .title(guestUnivName + "대표가자 대항전에 참가했습니다. ")
                .content(univBattle.getGuestUnivName() + "VS" + univBattle.getHostUnivName() + "대항전이 매칭되었습니다.")
                .relatedItemId(univBattle.getUnivBattleId())
                .build();
        notificationService.sendNotify(dto);


        return true;

    }


    /**
     * 대항전 일반 참가
     * 주최자와 참가자대표의 초대코드를 통해 참여가능
     * @param request
     * @return
     */
    @Override
    @Transactional
    public boolean attend(AttendRequest request) {

        boolean last = false;

        // 참가자 정보 조회
        Member member = memberRepository.findById(request.getMemberIdx())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 대항전 정보 조회
        UnivBattle univBattle = univBattleRepository.findById(request.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 초대 코드 체크
        if (!request.getInvitationCode().equals(univBattle.getInvitationCode())){
            throw new CustomException(CustomExceptionCode.INVALID_INVITE_CODE);
        }

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

        // 마지막 참가자일 경우 준비중으로 변경.
        if(totalParticipant == univBattle.getTeamPtcLimit() * 2  - 1){
            univBattle.setMatchStatus(MatchStatus.PREPARED);
            last = true;
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

        // 해당 대항전에 대한 채팅방을 설정합니다.
        // 게스트 채팅방 이름을 호스트 대학명으로 업데이트
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(0, univBattle.getUnivBattleId());
        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .customChatRoomName(univBattle.getHostUnivName() + " 대항전")
                .memberIdx(request.getMemberIdx())
                .build();

        chatMemberRepository.save(chatMember);

        // 호스트 채팅방의 이름을 게스트 대학명으로 업데이트.
        ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(univBattle.getHostLeader(), chatRoom.getChatRoomId());
        hostChatMember.setCustomChatRoomName(univBattle.getGuestUnivName() + " 대항전");
        chatMemberRepository.save(hostChatMember);

        if (last) {
            //@TODO  마지막 참가자일 경우 모든 참가자가 참가했다고 전송할것.

            // FCM 알림 전송 메서드 (주최자에게만 발송)
            FcmSendDto fcmSendDto = FcmSendDto.builder()
                    .token("dWVpAXGoS0-qW8txlowMKt:APA91bEUdfKJYNQYLTDppQVhwQtXoUfwhgYLnTEgoLhZmTXfY8YbK" +
                            "HeAhiTDoMxXHChr2mhb-eA3eNb0MPUpAHHwceXciW4FZhck-AfWSbHQmwkTHRljIuTFZAhhDYDRKqF2WIZMnpYL")
                    .title(univBattle.getGuestUnivName() + "대항전 전원 참가 완료!")
                    .body(univBattle.getHostUnivName() + "vs" + univBattle.getGuestUnivName() + "참가자 전원 참여완료!")
                    .build();
            try {
                fcmService.sendMessageTo(fcmSendDto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 알림 전송 메서드 (주최자에게만 발송)
            NotifyCreateReq dto = NotifyCreateReq.builder()
                    .type(MsgType.UNIV_BATTLE)
                    .isRead(false)
                    .receiver(univBattle.getHostLeader())
                    .title(univBattle.getGuestUnivName() + "대항전 전원 참가 완료!")
                    .content(univBattle.getGuestUnivName() + "VS" + univBattle.getHostUnivName() + "참가자 전원 참여완료!")
                    .relatedItemId(univBattle.getUnivBattleId())
                    .build();
            notificationService.sendNotify(dto);

        }

        return true;
    }

    @Override
    public List<UnivBattle> uList(int status) {

        return switch (status) {
            case 0 -> univBattleRepository.findAll();
            case 1 -> univBattleRepository.findByMatchStatus(MatchStatus.RECRUIT);
            case 2 -> univBattleRepository.findByMatchStatus(MatchStatus.WAITING);
            case 3 -> univBattleRepository.findByMatchStatus(MatchStatus.IN_PROGRESS);
            case 4 -> univBattleRepository.findByMatchStatus(MatchStatus.COMPLETED);
            default -> throw new CustomException(CustomExceptionCode.SERVER_ERROR);
        };

    }

    @Override
    public List<UnivBattleListRes> uList(Long univId) {

        List<UnivBattle> univBattleList = univBattleMapper.findByUnivId(univId);

        List<UnivBattleListRes> univBattleListResList = new ArrayList<>();

        for (UnivBattle x : univBattleList){
            String result = "";

            // 해당 대학의 승패여부 확인
            if(Objects.equals(x.getWinUniv(), univId)){
                result = "win";
            } else{
                result = "lose";
            }

            UnivBattleListRes univBattleListRes = UnivBattleListRes.builder()
                    .univBattleId(x.getUnivBattleId())
                    .eventId(x.getEventId())
                    .hostUnivName(x.getHostUnivName())
                    .guestUnivName(x.getGuestUnivName())
                    .hostUnivLogo(x.getHostUnivLogo())
                    .guestUnivLogo(x.getGuestUnivLogo())
                    .battleDate(x.getBattleDate())
                    .matchStartDt(x.getMatchStartDt())
                    .matchEndDt(x.getMatchEndDt())
                    .hostScore(x.getHostScore())
                    .guestScore(x.getGuestScore())
                    .result(result)
                    .build();

            univBattleListResList.add(univBattleListRes);
        }


        return univBattleListResList;
    }


    /**
     * 대항전 정보 조회
     * @param univBattleId
     * @return
     */
    @Override
    public Map<String, Object> info(long univBattleId) {

        UnivBattle univBattle = univBattleRepository.findById(univBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 대학별 참가자 리스트 추출
        List<Participant> HostparticipantList = participantRepository.findAllByUnivIdAndUnivBattleId(univBattle.getHostUniv(),univBattleId);
        List<Participant> GuestparticipantList = participantRepository.findAllByUnivIdAndUnivBattleId(univBattle.getGuestUniv(),univBattleId);

        int hostPtc = HostparticipantList.size();
        int guestPtc = GuestparticipantList.size();

        University hostuniversity = universityRepository.findById(univBattle.getHostUniv())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_UNIVERSITY));

        String hostUvName = hostuniversity.getSchoolName();
        String guestUvName = "";

        // 대항전에 대표참가자가 참여했다면 대학명 추출
        if(univBattle.getGuestUniv() != null) {
            University GuestUniversity = universityRepository.findById(univBattle.getGuestUniv())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_UNIVERSITY));
            guestUvName = GuestUniversity.getSchoolName();
        }


        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(0,univBattleId);
        long chatRoomId = chatRoom.getChatRoomId();


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

        List<Participant> participantList = participantRepository.findByUnivBattleId(univBattleId);
        // 경기 참여 인원 수와 경기 인원 수가 같을 경우에만 경기 시작.
        if(participantList.size() != univBattle.getTeamPtcLimit() * 2){
            throw new CustomException(CustomExceptionCode.INSUFFICIENT_MATCH_PLAYERS);
        }

        //@TODO 테스트를 위해 주석처리 실 배포때 사용할 메서드.
//        for (Participant participants : participantList){
//            Member member = memberRepository.findById(participants.getMemberIdx())
//                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
//            FcmSendDto fcmSendDto = FcmSendDto.builder()
//                    .token(member.getFcmToken())
//                    .title("대항전이 시작되었습니다.")
//                    .body(univBattle.getHostUnivName() + "vs" + univBattle.getGuestUnivName() + "경기가 시작되었습니다!")
//                    .build();
//            try {
//                fcmService.sendMessageTo(fcmSendDto);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        // 대항전 상태를 "진행중" 으로 업데이트
        univBattle.setMatchStatus(MatchStatus.IN_PROGRESS);
        univBattle.setMatchStartDt(LocalDateTime.now());
        univBattleRepository.save(univBattle);

        // FCM 알림 전송 메서드
        FcmSendDto fcmSendDto = FcmSendDto.builder()
                .token("dWVpAXGoS0-qW8txlowMKt:APA91bEUdfKJYNQYLTDppQVhwQtXoUfwhgYLnTEgoLhZmTXfY8YbK" +
                        "HeAhiTDoMxXHChr2mhb-eA3eNb0MPUpAHHwceXciW4FZhck-AfWSbHQmwkTHRljIuTFZAhhDYDRKqF2WIZMnpYL")
                .title("대항전이 시작되었습니다.")
                .body(univBattle.getHostUnivName() + "vs" + univBattle.getGuestUnivName() + "경기가 시작되었습니다!")
                .build();
        try {
            fcmService.sendMessageTo(fcmSendDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //@TODO 배포 전에 위 주석 메서드 부분에 추가할것. 지금은 따로 빼서 TEST
        for (Participant participants : participantList){
            Member member = memberRepository.findById(participants.getMemberIdx())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            NotifyCreateReq dto = NotifyCreateReq.builder()
                    .type(MsgType.UNIV_BATTLE)
                    .isRead(false)
                    .receiver(member.getMemberIdx())
                    .title("대항전이 시작되었습니다.")
                    .content(univBattle.getGuestUnivName() + "VS" + univBattle.getHostUnivName() + "경기 시작")
                    .relatedItemId(univBattle.getUnivBattleId())
                    .build();
            notificationService.sendNotify(dto);

        }

        return true;
    }

    /**
     * 경기 결과 요청 (주최팀 -> 참가팀)
     * 경기의 결과 기록은 주최측 대표자가 함.
     * 그 기록은 참가팀 대표자가 동의해야한다.
     * 요청에 대한 응답이 1시간이 넘으면 주최자측 기록으로 종료된다.
     * @param dto
     * @return
     */

    @Override
    @Transactional
    public boolean matchResultReq(MatchResultRequest dto) {
        UnivBattle univBattle = univBattleRepository.findById(dto.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        if (!Objects.equals(univBattle.getHostLeader(), dto.getHostLeader())){
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }


        // 경기 상태가 진행 중이 아닐 경우 예외처리
        if (univBattle.getMatchStatus() != MatchStatus.IN_PROGRESS) {
            throw new CustomException(CustomExceptionCode.NOT_IN_PROGRESS);
        }

        // 경기 점수와 승리한 대학교 기록 설정
        univBattle.setGuestScore(dto.getGuestScore());
        univBattle.setHostScore(dto.getHostScore());
        univBattle.setWinUniv(dto.getWinUniv());
        if(dto.getGuestScore() > dto.getHostScore()){
            univBattle.setLoseUniv(univBattle.getHostUniv());
        } else {
            univBattle.setLoseUniv(univBattle.getGuestUniv());
        }

        univBattleRepository.save(univBattle);


        /**
         * 주최자측 대항전 결과 전송에 대한 참가자 동의를 1시간 동안 안 받을 시
         * checkIncompleteMatch 메서드 실행 후 대항전 상태를 COMPLETE 로 변경.
         */
        // 1시간 이후에 실행될 스케줄링 작업을 생성합니다. (테스트용 1분 설정)
        ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
            log.info("Scheduled Task 실행");
            checkIncompleteMatch(univBattle.getUnivBattleId());
        }, 1, TimeUnit.MINUTES); // 1분 후에 실행

        // 예약된 작업을 관리 목록에 추가
        scheduledTasks.put(univBattle.getUnivBattleId(), scheduledFuture);

        Optional<Member> member = memberRepository.findById(univBattle.getGuestLeader());
        String fcmToken = member.get().getFcmToken();

        // FCM 알림 전송 메서드 (참가자대표에게 발송)
        FcmSendDto fcmSendDto = FcmSendDto.builder()
                .token(fcmToken)
                .title(univBattle.getHostUnivName() +  "경기 결과를 확인해주세요.")
                .body("1시간 안에 경기 결과에 대한 응답이 없을 시 주최측 경기결과로 경기가 종료됩니다.")
                .build();
        try {
            fcmService.sendMessageTo(fcmSendDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 알림 전송 메서드 (주최자에게만 발송)
        NotifyCreateReq req = NotifyCreateReq.builder()
                .type(MsgType.UNIV_BATTLE)
                .isRead(false)
                .receiver(univBattle.getHostLeader())
                .title(univBattle.getHostUnivName() +  "경기 결과를 확인해주세요.")
                .content("1시간 안에 경기 결과에 대한 응답이 없을 시 주최측 경기결과로 경기가 종료됩니다.")
                .relatedItemId(univBattle.getUnivBattleId())
                .build();
        notificationService.sendNotify(req);

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
    @Transactional
    public boolean matchResultRes(MatchResultResponse dto) {
        UnivBattle univBattle = univBattleRepository.findById(dto.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 이미 경기가 완료된 상태일 경우 예외 처리
        if (univBattle.getMatchStatus() == MatchStatus.COMPLETED) {
            throw new CustomException(CustomExceptionCode.ALREADY_END_MATCH);
        }

        if (!Objects.equals(univBattle.getGuestLeader(), dto.getMemberIdx())){
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }

        // 주최자측 결과 보고에 응답했는 경우 관리 목록에서 해당 경기의 스케줄링 작업을 가져와 취소
        ScheduledFuture<?> future = scheduledTasks.remove(univBattle.getUnivBattleId());
        if (future != null) {
            future.cancel(false); // 작업 취소
            log.info("Scheduled Task 취소 시도");
        }

        // true 로 반응한 경우 경기결과에 문제가 없으니 COMPLETED 처리
        if (dto.isResultYN()) {

            // 대학 랭킹 점수 update
            // 승리 팀 업데이트
            int winExistence = rankMapper.checkExistence(univBattle.getWinUniv(), univBattle.getEventId());
            if (winExistence == 0) {
                rankMapper.insertRank(univBattle.getWinUniv(), univBattle.getEventId(), 10L, 1L, 0L);
            } else {
                rankMapper.updateWinRank(univBattle.getWinUniv(), univBattle.getEventId());
            }

            // 패배 팀 업데이트
            int loseExistence = rankMapper.checkExistence(univBattle.getLoseUniv(), univBattle.getEventId());
            if (loseExistence == 0) {
                rankMapper.insertRank(univBattle.getLoseUniv(), univBattle.getEventId(), 0L, 0L, 1L);
            } else {
                rankMapper.updateLoseRank(univBattle.getLoseUniv(), univBattle.getEventId());
            }

//            univBattleMapper.updateRankPoints(univBattle.getWinUniv(), univBattle.getLoseUniv());
            univBattle.setMatchStatus(MatchStatus.COMPLETED);

            Optional<Member> member = memberRepository.findById(univBattle.getHostLeader());
            String fcmToken = member.get().getFcmToken();

            List<Participant> participantList = participantRepository.findByUnivBattleId(univBattle.getUnivBattleId());
            // 경기 참여 인원 수와 경기 인원 수가 같을 경우에만 경기 시작.
            if(participantList.size() != univBattle.getTeamPtcLimit() * 2){
                throw new CustomException(CustomExceptionCode.INSUFFICIENT_MATCH_PLAYERS);
            }

            for (Participant participants : participantList){
                Member member2 = memberRepository.findById(participants.getMemberIdx())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

                NotifyCreateReq req = NotifyCreateReq.builder()
                        .type(MsgType.UNIV_BATTLE)
                        .isRead(false)
                        .receiver(member2.getMemberIdx())
                        .title(univBattle.getGuestUnivName() +  "대항전이  종료되었습니다.")
                        .content(univBattle.getGuestUnivName() + "VS" + univBattle.getHostUnivName() + "대항전이 종료되었습니다.")
                        .relatedItemId(univBattle.getUnivBattleId())
                        .build();
                notificationService.sendNotify(req);

            }


            //@TODO 테스트를 위해 주석처리 실 배포때 사용할 메서드.
//        for (Participant participants : participantList){
//            Member member = memberRepository.findById(participants.getMemberIdx())
//                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
//            FcmSendDto fcmSendDto = FcmSendDto.builder()
//                    .token(member.getFcmToken())
//                    .title("대항전이 시작되었습니다.")
//                    .body(univBattle.getHostUnivName() + "vs" + univBattle.getGuestUnivName() + "경기가 시작되었습니다!")
//                    .build();
//            try {
//                fcmService.sendMessageTo(fcmSendDto);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }


        }
        // false 로 반응한 경우 점수 및 승리팀 기록 초기화.
        else {
            univBattle.setGuestScore(null);
            univBattle.setHostScore(null);
            univBattle.setWinUniv(null);

            // FCM 알림 전송 메서드 (주최자에게만 발송)
            FcmSendDto fcmSendDto = FcmSendDto.builder()
                    .token("dWVpAXGoS0-qW8txlowMKt:APA91bEUdfKJYNQYLTDppQVhwQtXoUfwhgYLnTEgoLhZmTXfY8YbK" +
                            "HeAhiTDoMxXHChr2mhb-eA3eNb0MPUpAHHwceXciW4FZhck-AfWSbHQmwkTHRljIuTFZAhhDYDRKqF2WIZMnpYL")
                    .title(univBattle.getGuestUnivName() + "대표자가 경기결과에 동의하지 않았습니다.")
                    .body("경기 결과를 다시 제출해주세요.")
                    .build();
            try {
                fcmService.sendMessageTo(fcmSendDto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 알림 전송 메서드 (주최자에게만 발송)
            NotifyCreateReq req = NotifyCreateReq.builder()
                    .type(MsgType.UNIV_BATTLE)
                    .isRead(false)
                    .receiver(univBattle.getHostLeader())
                    .title(univBattle.getGuestUnivName() + "대표자가 경기결과에 동의하지 않았습니다.")
                    .content("경기 결과를 다시 제출해주세요.")
                    .relatedItemId(univBattle.getUnivBattleId())
                    .build();
            notificationService.sendNotify(req);


        }

        univBattleRepository.save(univBattle);


        return true;


    }

    /**
     * 경기 종료 메서드
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

            // 랭크 점수 업데이트 mapper
            // 승리 팀 업데이트
            int winExistence = rankMapper.checkExistence(univBattle.getWinUniv(), univBattle.getEventId());
            if (winExistence == 0) {
                rankMapper.insertRank(univBattle.getWinUniv(), univBattle.getEventId(), 10L, 1L, 0L);
            } else {
                rankMapper.updateWinRank(univBattle.getWinUniv(), univBattle.getEventId());
            }

            // 패배 팀 업데이트
            int loseExistence = rankMapper.checkExistence(univBattle.getLoseUniv(), univBattle.getEventId());
            if (loseExistence == 0) {
                rankMapper.insertRank(univBattle.getLoseUniv(), univBattle.getEventId(), 0L, 0L, 1L);
            } else {
                rankMapper.updateLoseRank(univBattle.getLoseUniv(), univBattle.getEventId());
            }

//            univBattleMapper.updateRankPoints(univBattle.getWinUniv(), univBattle.getLoseUniv());
            log.info("경기 종료 처리 완료");
        }
    }


    /**
     * 초대 코드 생성기
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