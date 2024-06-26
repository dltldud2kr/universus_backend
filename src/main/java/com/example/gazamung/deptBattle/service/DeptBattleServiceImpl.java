package com.example.gazamung.deptBattle.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.MatchStatus;
import com.example.gazamung._enum.MsgType;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatMessage.ChatMessage;
import com.example.gazamung.chat.chatMessage.ChatMessageRepository;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomRepository;
import com.example.gazamung.department.entity.Department;
import com.example.gazamung.department.repository.DepartmentRepository;
import com.example.gazamung.deptBattle.dto.*;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.deptBattle.repository.DeptBattleRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.fcmSend.FcmSendDto;
import com.example.gazamung.fcmSend.FcmService;
import com.example.gazamung.mapper.DeptBattleMapper;
import com.example.gazamung.mapper.RankMapper;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.service.NotificationService;
import com.example.gazamung.participant.entity.Participant;
import com.example.gazamung.participant.repository.ParticipantRepository;
import com.example.gazamung.univBattle.dto.UnivBattleListRes;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.service.UnivBattleServiceImpl;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptBattleServiceImpl implements DeptBattleService {

    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final DeptBattleRepository deptBattleRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final DepartmentRepository departmentRepository;
    private final FcmService fcmService;
    private final NotificationService notificationService;
    private final RankMapper rankMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final DeptBattleMapper deptBattleMapper;

    // 스케줄러 생성
    private final ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
    // 예약 작업 관리를 위한 ConcurrentHashMap
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks2 = new ConcurrentHashMap<>();



    /**
     * 과 vs 과 대항전 생성
     * @param request
     * @return
     */
    @Override
    public boolean create(DeptBattleCreateRequest request) {
        Member member = validateMember(request.getHostLeader());
        University university = validateUniversity(member.getUnivId());
        Department department = validateDepartment(member.getDeptId());

        DeptBattle deptBattle = buildDeptBattle(request, member, university, department);
        deptBattleRepository.save(deptBattle);

        addParticipant(member, deptBattle);
        createChatRoomWithMember(member, deptBattle);

        return true;
    }


    @Override
    public boolean GuestLeaderAttend(DeptGuestLeaderAttendRequest request) {

// 대항전 정보를 검증 후 존재하지 않을 경우 예외 발생
        DeptBattle deptBattle = deptBattleRepository.findById(request.getDeptBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 게스트 리더의 유효성을 검사 후 조건에 맞지 않으면 예외 발생
        Member guest = memberRepository.findById(request.getGuestLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        if (!Objects.equals(deptBattle.getUnivId(), guest.getUnivId())) {
            throw new CustomException(CustomExceptionCode.NOT_SAME_UNIVERSITY);
        }

        if (deptBattle.getGuestLeader() != null) {
            throw new CustomException(CustomExceptionCode.REPRESENTATIVE_ALREADY_EXISTS);
        }

        // 동일 대학교 내의 다른 과 검증 후, 같은 과일 경우 예외를 발생
        if (Objects.equals(deptBattle.getHostDept(), guest.getDeptId())) {
            throw new CustomException(CustomExceptionCode.SAME_DEPARTMENT);
        }

        // 대항전 게스트 리더 관련 정보를 업데이트
        Department department = departmentRepository.findById(guest.getDeptId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_DEPARTMENT));

        deptBattle.setGuestLeader(guest.getMemberIdx());
        deptBattle.setGuestDept(guest.getDeptId());
        deptBattle.setGuestDeptName(department.getDeptName());
        deptBattle.setMatchStatus(MatchStatus.WAITING);
        deptBattle.setInvitationCode(generateRandomString(8));
        deptBattleRepository.save(deptBattle);

        // 참가자 관리 로직을 처리. 참가자 수에 따라 대항전의 상태를 업데이트.
        int totalParticipant = participantRepository.countByDeptBattleId(deptBattle.getDeptBattleId());
        if (totalParticipant == deptBattle.getTeamPtcLimit() * 2 - 1) {
            deptBattle.setMatchStatus(MatchStatus.PREPARED);
            deptBattleRepository.save(deptBattle);
        }

        Participant participant = Participant.builder()
                .memberIdx(guest.getMemberIdx())
                .nickName(guest.getNickname())
                .userName(guest.getName())
                .deptBattleId(deptBattle.getDeptBattleId())
                .deptId(guest.getDeptId())
                .univId(deptBattle.getUnivId())
                .build();
        participantRepository.save(participant);

        // 관련 채팅방 멤버 정보를 관리. 채팅방 이름 업데이트 포함.
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(1, deptBattle.getDeptBattleId());
        if (chatRoom != null) {
            Member host = memberRepository.findById(deptBattle.getHostLeader())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            ChatMember chatMember = ChatMember.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .chatRoomType(1)
                    .customChatRoomName(deptBattle.getHostDeptName() + " 대항전")
                    .memberIdx(guest.getMemberIdx())
                    .chatRoomImg(host.getUnivLogoImg())
                    .build();
            chatMemberRepository.save(chatMember);

            ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(deptBattle.getHostLeader(), chatRoom.getChatRoomId());
            if (hostChatMember != null) {
                hostChatMember.setCustomChatRoomName(deptBattle.getGuestDeptName() + " 대항전");
                hostChatMember.setChatRoomImg(guest.getUnivLogoImg());
                chatMemberRepository.save(hostChatMember);
            }
        }

        ChatMessage chatMessage =  ChatMessage.builder()
                .chatRoomType(chatRoom.getChatRoomType())
                .chatRoomId(chatRoom.getChatRoomId())
                .content(guest.getNickname() + "님이 입장하셨습니다.")
                .nickname(" ")
                .regDt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        Member member = memberRepository.findById(deptBattle.getHostLeader())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        String fcmToken = member.getFcmToken();

        // FCM 알림 전송 메서드 (주최자에게만 발송)
        if (fcmToken != null && !fcmToken.isEmpty()) {
            FcmSendDto fcmSendDto = FcmSendDto.builder()
                    .token(member.getFcmToken())
                    .title(deptBattle.getGuestDeptName() + "대표자가 대항전에 참가했습니다.")
                    .body(deptBattle.getHostDeptName() + "vs" + deptBattle.getGuestDeptName() + "대항전이 매칭되었습니다.")
                    .target("deptBattle/info")
                    .data(String.valueOf(deptBattle.getDeptBattleId()))
                    .build();
            try {
                fcmService.sendMessageTo(fcmSendDto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 알림 전송 메서드 (주최자에게만 발송)
        NotifyCreateReq dto = NotifyCreateReq.builder()
                .type(MsgType.DEPT_BATTLE)
                .isRead(false)
                .receiver(deptBattle.getHostLeader())
                .title(deptBattle.getGuestDeptName() + "대표자가 대항전에 참가했습니다. ")
                .content(deptBattle.getGuestDeptName() + "VS" + deptBattle.getHostDeptName() + "대항전이 매칭되었습니다.")
                .relatedItemId(deptBattle.getDeptBattleId())
                .build();
        notificationService.sendNotify(dto);


        return true;
    }

    @Override
    public List<DeptBattle> list(int status, Long memberIdx) {

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() ->new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        long univId = member.getUnivId();

        return switch (status) {
            case 0 -> deptBattleRepository.findAllByUnivIdOrderByRegDtDesc(univId);
            case 1 -> deptBattleRepository.findByMatchStatusAndUnivIdOrderByRegDtDesc(MatchStatus.RECRUIT, univId);
            case 2 -> deptBattleRepository.findByMatchStatusAndUnivIdOrderByRegDtDesc(MatchStatus.WAITING, univId);
            case 3 -> deptBattleRepository.findByMatchStatusAndUnivIdOrderByRegDtDesc(MatchStatus.IN_PROGRESS, univId);
            case 4 -> deptBattleRepository.findByMatchStatusAndUnivIdOrderByRegDtDesc(MatchStatus.COMPLETED, univId);
            default -> throw new CustomException(CustomExceptionCode.SERVER_ERROR);
        };
    }

    @Override
    public Map<String, Object> info(long deptBattleId) {

        DeptBattle deptBattle = deptBattleRepository.findById(deptBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));


        int hostPtc = participantRepository.countByDeptBattleIdAndDeptId(deptBattleId,deptBattle.getHostDept());
        int guestPtc = participantRepository.countByDeptBattleIdAndDeptId(deptBattleId,deptBattle.getGuestDept());

        List<Participant> hostparticipantList = participantRepository.findAllByDeptIdAndDeptBattleId(deptBattle.getHostDept(),deptBattleId);
        List<Participant> guestparticipantList = participantRepository.findAllByDeptIdAndDeptBattleId(deptBattle.getGuestDept(),deptBattleId);

        University university = universityRepository.findById(deptBattle.getUnivId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_UNIVERSITY));

        String uvName = university.getSchoolName();

        Department hostDept = departmentRepository.findById(deptBattle.getHostDept())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_DEPARTMENT));



        String hostDeptName = hostDept.getDeptName();
        String guestDeptName = "";

        if(deptBattle.getGuestDept() != null){
            Department guestDept = departmentRepository.findById(deptBattle.getGuestDept())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_DEPARTMENT));
            guestDeptName = guestDept.getDeptName();
        }

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(1,deptBattleId);
        long chatRoomId = chatRoom.getChatRoomId();

        // 응답용 Map 생성 및 값 추가
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> hostTeam = new HashMap<>();
        hostTeam.put("hostDeptName",hostDeptName);
        hostTeam.put("hostPtcCnt", hostPtc);
        hostTeam.put("hostPtcList", hostparticipantList);

        Map<String, Object> guestTeam = new HashMap<>();
        guestTeam.put("guestDeptName", guestDeptName);
        guestTeam.put("guestPtcCnt", guestPtc);               // 참가팀 회원 수
        guestTeam.put("guestPtcList", guestparticipantList);    // 참가팀 회원 리스트

        response.put("HostTeam", hostTeam);
        response.put("GuestTeam", guestTeam);
        response.put("deptBattle", deptBattle);
        response.put("chatRoomType", chatRoom.getChatRoomType());
        response.put("chatRoomId", chatRoomId);

        if(deptBattle.getWinDept() != null) {
            String winUniv = departmentRepository.findById(deptBattle.getWinDept()).orElse(null).getDeptName();
            response.put("winDeptName", winUniv);
        }


        return response;
    }

    @Override
    public boolean matchStart(long deptBattleId) {

        DeptBattle deptBattle = deptBattleRepository.findById(deptBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 준비가 안 된 경우 경기 시작을 할 수 없음.
        if(deptBattle.getMatchStatus()!= MatchStatus.PREPARED){
            throw new CustomException(CustomExceptionCode.CANNOT_START_MATCH);
        }
        // 경기 참여 인원 수와 경기 인원 수가 같을 경우에만 경기 시작.
        List<Participant> participantList = participantRepository.findByDeptBattleId(deptBattleId);
        if(participantList.size() != deptBattle.getTeamPtcLimit() * 2){
            throw new CustomException(CustomExceptionCode.INSUFFICIENT_MATCH_PLAYERS);
        }

        deptBattle.setMatchStatus(MatchStatus.IN_PROGRESS);
        deptBattle.setMatchStartDt(LocalDateTime.now());
        deptBattleRepository.save(deptBattle);


        //@TODO 테스트를 위해 주석처리 실 배포때 사용할 메서드.
        for (Participant participants : participantList){
            Member member = memberRepository.findById(participants.getMemberIdx())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
            String fcmToken = member.getFcmToken();

            if (fcmToken != null && !fcmToken.isEmpty()) {

                FcmSendDto fcmSendDto = FcmSendDto.builder()
                        .token(fcmToken)
                        .title("대항전이 시작되었습니다.")
                        .body(deptBattle.getHostDept() + "vs" + deptBattle.getGuestDept() + "경기가 시작되었습니다!")
                        .target("deptBattle/info")
                        .data(String.valueOf(deptBattle.getDeptBattleId()))
                        .build();
                try {
                    fcmService.sendMessageTo(fcmSendDto);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //@TODO 배포 전에 위 주석 메서드 부분에 추가할것. 지금은 따로 빼서 TEST
        for (Participant participants : participantList){
            Member member = memberRepository.findById(participants.getMemberIdx())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            NotifyCreateReq dto = NotifyCreateReq.builder()
                    .type(MsgType.DEPT_BATTLE)
                    .isRead(false)
                    .receiver(member.getMemberIdx())
                    .title("대항전이 시작되었습니다.")
                    .content(deptBattle.getGuestDeptName() + "VS" + deptBattle.getHostDeptName() + "경기 시작")
                    .relatedItemId(deptBattle.getDeptBattleId())
                    .build();
            notificationService.sendNotify(dto);

        }

        return true;
    }

    @Override
    public boolean matchResultReq(DeptMatchResultReq dto) {

        DeptBattle deptBattle = deptBattleRepository.findById(dto.getDeptBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        if (!Objects.equals(deptBattle.getHostLeader(), dto.getHostLeader())){
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }

        // 경기 상태가 진행 중이 아닐 경우 예외처리
        if (deptBattle.getMatchStatus() != MatchStatus.IN_PROGRESS) {
            throw new CustomException(CustomExceptionCode.NOT_IN_PROGRESS);
        }

        // 경기 점수와 승리한 대학교 기록 설정
        deptBattle.setGuestScore(dto.getGuestScore());
        deptBattle.setHostScore(dto.getHostScore());
        deptBattle.setWinDept(dto.getWinDept());
        deptBattle.setMatchEndDt(LocalDateTime.now());
        if(dto.getGuestScore() > dto.getHostScore()){
            deptBattle.setLoseDept(deptBattle.getHostDept());
        } else {
            deptBattle.setLoseDept(deptBattle.getGuestDept());
        }

        deptBattleRepository.save(deptBattle);

        /**
         * 주최자측 대항전 결과 전송에 대한 참가자 동의를 1시간 동안 안 받을 시
         * checkIncompleteMatch 메서드 실행 후 대항전 상태를 COMPLETE 로 변경.
         */
        // 1시간 이후에 실행될 스케줄링 작업을 생성합니다. (테스트용 1분 설정)
        ScheduledFuture<?> scheduledFuture = scheduler2.schedule(() -> {
            log.info("Scheduled Task 실행");
            checkIncompleteMatch(deptBattle.getDeptBattleId());
        }, 1, TimeUnit.MINUTES); // 1분 후에 실행

        // 예약된 작업을 관리 목록에 추가
        scheduledTasks2.put(deptBattle.getDeptBattleId(), scheduledFuture);

        Member member = memberRepository.findById(deptBattle.getGuestLeader())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        String fcmToken = member.getFcmToken();

        // FCM 알림 전송 메서드 (참가자대표에게 발송)
        if (fcmToken != null && !fcmToken.isEmpty()) {

            FcmSendDto fcmSendDto = FcmSendDto.builder()
                    .token(fcmToken)
                    .title(deptBattle.getHostDeptName() + "경기 결과를 확인해주세요.")
                    .body("1시간 안에 경기 결과에 대한 응답이 없을 시 주최측 경기결과로 경기가 종료됩니다.")
                    .target("deptBattle/resultRes")
                    .data(deptBattle.getDeptBattleId().toString())
                    .build();
            try {
                fcmService.sendMessageTo(fcmSendDto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 알림 전송 메서드 (주최자에게만 발송)
        NotifyCreateReq req = NotifyCreateReq.builder()
                .type(MsgType.DEPT_BATTLE)
                .isRead(false)
                .receiver(deptBattle.getHostLeader())
                .title(deptBattle.getHostDeptName() +  "경기 결과를 확인해주세요.")
                .content("1시간 안에 경기 결과에 대한 응답이 없을 시 주최측 경기결과로 경기가 종료됩니다.")
                .relatedItemId(deptBattle.getDeptBattleId())
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
    public boolean matchResultRes(DeptMatchResultRes dto) {

        DeptBattle deptBattle = deptBattleRepository.findById(dto.getDeptBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 이미 경기가 완료된 상태일 경우 예외 처리
        if (deptBattle.getMatchStatus() == MatchStatus.COMPLETED) {
            throw new CustomException(CustomExceptionCode.ALREADY_END_MATCH);
        }

        // 대표자가 맞는지 검사.
        System.out.println("guestLeader : " + deptBattle.getGuestLeader() + " memberIdx : " + dto.getMemberIdx() );
        if (!Objects.equals(deptBattle.getGuestLeader(), dto.getMemberIdx())){
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }


        // 주최자측 결과 보고에 응답했는 경우 관리 목록에서 해당 경기의 스케줄링 작업을 가져와 취소
        ScheduledFuture<?> future = scheduledTasks2.remove(deptBattle.getDeptBattleId());
        if (future != null) {
            future.cancel(false); // 작업 취소
            log.info("Scheduled Task 취소 시도");
        }

        // true 로 반응한 경우 경기결과에 문제가 없으니 COMPLETED 처리
        if (dto.isResultYN()) {
            // 대학 랭킹 점수 update
            // 승리 팀 업데이트
            int winExistence = rankMapper.checkDeptExistence(deptBattle.getUnivId(), deptBattle.getWinDept(), deptBattle.getEventId());
            if (winExistence == 0) {
                rankMapper.insertDeptRank(deptBattle.getUnivId(), deptBattle.getWinDept(), deptBattle.getEventId(), 10L, 1L, 0L);
                System.out.println("Inserted new winning department rank");
            } else {
                rankMapper.updateDeptWinRank(deptBattle.getUnivId(), deptBattle.getWinDept(), deptBattle.getEventId());
                System.out.println("Updated existing winning department rank");
            }

            // 패배 팀 업데이트
            int loseExistence = rankMapper.checkDeptExistence(deptBattle.getUnivId(), deptBattle.getLoseDept(), deptBattle.getEventId());
            if (loseExistence == 0) {
                rankMapper.insertDeptRank(deptBattle.getUnivId(), deptBattle.getLoseDept(), deptBattle.getEventId(), 0L, 0L, 1L);
                System.out.println("Inserted new losing department rank");
            } else {
                rankMapper.updateDeptLoseRank(deptBattle.getUnivId(), deptBattle.getLoseDept(), deptBattle.getEventId());
                System.out.println("Updated existing losing department rank");
            }

            deptBattle.setMatchStatus(MatchStatus.COMPLETED);

            List<Participant> participantList = participantRepository.findByDeptBattleId(deptBattle.getDeptBattleId());
            // 경기 참여 인원 수와 경기 인원 수가 같을 경우에만 경기 시작.

            System.out.println("partp.size = " + participantList.size() + " dept ptcmember : " + deptBattle.getTeamPtcLimit() * 2);
            if(participantList.size() != deptBattle.getTeamPtcLimit() * 2){
                throw new CustomException(CustomExceptionCode.INSUFFICIENT_MATCH_PLAYERS);
            }

            for (Participant participants : participantList){
                Member member2 = memberRepository.findById(participants.getMemberIdx())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));


                NotifyCreateReq req = NotifyCreateReq.builder()
                        .type(MsgType.DEPT_BATTLE)
                        .isRead(false)
                        .receiver(member2.getMemberIdx())
                        .title("대항전이  종료되었습니다.")
                        .content(deptBattle.getGuestDeptName() + "VS" + deptBattle.getHostDeptName() + "대항전이 종료되었습니다.")
                        .relatedItemId(deptBattle.getDeptBattleId())
                        .build();
                notificationService.sendNotify(req);

            }


            //@TODO 테스트를 위해 주석처리 실 배포때 사용할 메서드.
            for (Participant participants : participantList){
                Member partiMember = memberRepository.findById(participants.getMemberIdx())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
                String fcmToken = partiMember.getFcmToken();

                if (fcmToken != null && !fcmToken.isEmpty()) {
                    FcmSendDto fcmSendDto = FcmSendDto.builder()
                            .token(fcmToken)
                            .title("대항전 결과를 확인해주세요!")
                            .body("경기 결과를 확인해주세요!")
                            .target("deptBattle/info")
                            .data(String.valueOf(deptBattle.getDeptBattleId()))
                            .build();
                    try {
                        fcmService.sendMessageTo(fcmSendDto);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
        // false 로 반응한 경우 점수 및 승리팀 기록 초기화.
        else {
            deptBattle.setGuestScore(null);
            deptBattle.setHostScore(null);
            deptBattle.setMatchEndDt(null);
            deptBattle.setWinDept(null);


            Member member = memberRepository.findById(deptBattle.getHostLeader())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            String fcmToken = member.getFcmToken();

            // FCM 알림 전송 메서드 (주최자에게만 발송)
            if (fcmToken != null && !fcmToken.isEmpty()) {

                FcmSendDto fcmSendDto = FcmSendDto.builder()
                        .token(member.getFcmToken())
                        .title(deptBattle.getGuestDeptName() + "대표자가 경기결과에 동의하지 않았습니다.")
                        .body("경기 결과를 다시 제출해주세요.")
                        .target("deptBattle/info")
                        .data(String.valueOf(deptBattle.getDeptBattleId()))
                        .build();
                try {
                    fcmService.sendMessageTo(fcmSendDto);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // 알림 전송 메서드 (주최자에게만 발송)
            NotifyCreateReq req = NotifyCreateReq.builder()
                    .type(MsgType.DEPT_BATTLE)
                    .isRead(false)
                    .receiver(deptBattle.getHostLeader())
                    .title(deptBattle.getGuestDeptName() + "대표자가 경기결과에 동의하지 않았습니다.")
                    .content("경기 결과를 다시 제출해주세요.")
                    .relatedItemId(deptBattle.getDeptBattleId()
                    )
                    .build();
            notificationService.sendNotify(req);
        }

        deptBattleRepository.save(deptBattle);


        return true;
    }

    @Override
    public List<DeptBattleListRes> dList(Long memberIdx,Long deptId) {

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
        long univId = member.getUnivId();
        List<DeptBattle> deptBattleList = deptBattleMapper.findByDeptAndUnivId(univId,deptId);

        List<DeptBattleListRes> deptBattleListResList = new ArrayList<>();

        for (DeptBattle x : deptBattleList){
            String result = "";

            // 해당 대학의 승패여부 확인
            if(Objects.equals(x.getWinDept(), univId)){
                result = "win";
            } else{
                result = "lose";
            }

            DeptBattleListRes deptBattleListRes = DeptBattleListRes.builder()
                    .deptBattleId(x.getDeptBattleId())
                    .eventId(x.getEventId())
                    .hostDeptName(x.getHostDeptName())
                    .guestDeptName(x.getGuestDeptName())
                    .univLogo(x.getUnivLogo())
                    .battleDate(x.getBattleDate())
                    .matchStartDt(x.getMatchStartDt())
                    .matchEndDt(x.getMatchEndDt())
                    .hostScore(x.getHostScore())
                    .guestScore(x.getGuestScore())
                    .result(result)
                    .build();

            deptBattleListResList.add(deptBattleListRes);
        }


        return deptBattleListResList;
    }


    @Override
    public boolean attend(DeptBattleAttendRequest request) {

        boolean last = false;

        // 참가자 정보 조회
        Member member = memberRepository.findById(request.getMemberIdx())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 대항전 정보 조회
        DeptBattle deptBattle = deptBattleRepository.findById(request.getDeptBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 이미 진행중인 경우 참가 불가
        if(deptBattle.getMatchStatus() == MatchStatus.IN_PROGRESS || deptBattle.getMatchStatus() == MatchStatus.COMPLETED){
            throw new CustomException(CustomExceptionCode.ALREADY_IN_PROGRESS);
        }

        // 이미 참가한 경우 예외 처리
        boolean alreadyAttended = participantRepository.existsByMemberIdxAndDeptBattleId(member.getMemberIdx(), deptBattle.getDeptBattleId());
        if (alreadyAttended) {
            throw new CustomException(CustomExceptionCode.ALREADY_ATTENDED);
        }

        // 참가인원 초과 여부 체크
        int totalParticipant = participantRepository.countByUnivBattleId(deptBattle.getDeptBattleId());
        if (totalParticipant >= deptBattle.getTeamPtcLimit() * 2) {
            throw new CustomException(CustomExceptionCode.EXCEEDED_TOTAL_CAPACITY);
        }

        // 과별 인원 초과 여부 체크
        int univTotalParticipant = participantRepository.countByDeptBattleIdAndDeptId(deptBattle.getDeptBattleId(), member.getUnivId());
        if (univTotalParticipant >= deptBattle.getTeamPtcLimit()) {
            throw new CustomException(CustomExceptionCode.EXCEEDED_DEPT_CAPACITY);
        }

        // 참가 코드 체크
        if (!request.getInvitationCode().equals(deptBattle.getInvitationCode())){
            throw new CustomException(CustomExceptionCode.INVALID_INVITE_CODE);
        }

        // 마지막 참가자일 경우 대기중으로 변경.
        if(totalParticipant == deptBattle.getTeamPtcLimit() * 2 - 1){
            deptBattle.setMatchStatus(MatchStatus.PREPARED);
            last = true;
        }

        // 참가자 저장
        Participant participant = Participant.builder()
                .memberIdx(request.getMemberIdx())
                .deptBattleId(request.getDeptBattleId())
                .univId(member.getUnivId())
                .deptId(member.getDeptId()) // 해당 회원 deptId 추가
                .userName(member.getName())
                .nickName(member.getNickname())
                .build();
        participantRepository.save(participant);

        // 해당 대항전에 대한 채팅방을 설정합니다.
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(1, deptBattle.getDeptBattleId());
        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .customChatRoomName(deptBattle.getHostDeptName() + "대항전")
                .memberIdx(request.getMemberIdx())
                .build();

        chatMemberRepository.save(chatMember);

        // 호스트 채팅방의 이름을 게스트 부서 이름으로 업데이트합니다.
        ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(deptBattle.getHostLeader(), chatRoom.getChatRoomId());
        hostChatMember.setCustomChatRoomName(deptBattle.getGuestDeptName() + "대항전");
        chatMemberRepository.save(hostChatMember);

        Member HostMember = memberRepository.findById(deptBattle.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        String fcmToken = HostMember.getFcmToken();

        ChatMessage chatMessage =  ChatMessage.builder()
                .chatRoomType(chatRoom.getChatRoomType())
                .chatRoomId(chatRoom.getChatRoomId())
                .content(member.getNickname() + "님이 입장하셨습니다.")
                .nickname(" ")
                .regDt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        if (last) {
            //@TODO  마지막 참가자일 경우 모든 참가자가 참가했다고 전송할것.


            // FCM 알림 전송 메서드 (주최자에게만 발송)
            if (fcmToken != null && !fcmToken.isEmpty()) {

                FcmSendDto fcmSendDto = FcmSendDto.builder()
                        .token(fcmToken)
                        .title("대항전 전원 참가 완료!")
                        .body(deptBattle.getHostDept() + "vs" + deptBattle.getGuestDept() + "참가자 전원 참여완료!")
                        .target("deptBattle/info")
                        .data(String.valueOf(deptBattle.getDeptBattleId()))
                        .build();
                try {
                    fcmService.sendMessageTo(fcmSendDto);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // 알림 전송 메서드 (주최자에게만 발송)
            NotifyCreateReq dto = NotifyCreateReq.builder()
                    .type(MsgType.UNIV_BATTLE)
                    .isRead(false)
                    .receiver(deptBattle.getHostLeader())
                    .title("대항전 전원 참가 완료!")
                    .content(deptBattle.getGuestDept() + "VS" + deptBattle.getHostDept() + "참가자 전원 참여완료!")
                    .relatedItemId(deptBattle.getDeptBattleId())
                    .build();
            notificationService.sendNotify(dto);

        }

        return true;
    }




    private Member validateMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER, "Member not found with ID: " + memberId));
    }

    private University validateUniversity(Long universityId) {
        return universityRepository.findById(universityId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND, "University not found with ID: " + universityId));
    }

    private Department validateDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_DEPARTMENT, "Department not found with ID: " + departmentId));
    }

    private DeptBattle buildDeptBattle(DeptBattleCreateRequest request, Member member, University university, Department department) {
        return DeptBattle.builder()
                .hostLeader(member.getMemberIdx())
                .eventId(request.getEventId())
                .univId(member.getUnivId())
                .hostDept(member.getDeptId())
                .content(request.getContent())
                .cost(request.getCost())
                .battleDate(request.getBattleDate())
                .teamPtcLimit(request.getTeamPtcLimit())
                .lat(request.getLat())
                .lng(request.getLng())
                .place(request.getPlace())
                .matchStatus(MatchStatus.RECRUIT)
                .regDt(LocalDateTime.now())
                .hostDeptName(department.getDeptName())
                .univLogo(university.getLogoImg())
                .build();
    }

    private void addParticipant(Member member, DeptBattle deptBattle) {
        Participant participant = Participant.builder()
                .memberIdx(member.getMemberIdx())
                .deptBattleId(deptBattle.getDeptBattleId())
                .userName(member.getName())
                .nickName(member.getNickname())
                .univId(member.getUnivId())
                .deptId(member.getDeptId())
                .build();
        participantRepository.save(participant);
    }

    private void createChatRoomWithMember(Member member, DeptBattle deptBattle) {
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomType(1)
                .dynamicId(deptBattle.getDeptBattleId()).build();
        chatRoomRepository.save(chatRoom);

        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .memberIdx(member.getMemberIdx())
                .build();
        chatMemberRepository.save(chatMember);
    }

    private DeptBattle validateDeptBattle(Long deptBattleId) {
        return deptBattleRepository.findById(deptBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));
    }

    private Member validateGuest(Long guestLeaderId, DeptBattle deptBattle) {
        Member guest = memberRepository.findById(guestLeaderId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        if (!Objects.equals(deptBattle.getUnivId(), guest.getUnivId())) {
            throw new CustomException(CustomExceptionCode.NOT_SAME_UNIVERSITY);
        }

        if (deptBattle.getGuestLeader() != null) {
            throw new CustomException(CustomExceptionCode.REPRESENTATIVE_ALREADY_EXISTS);
        }

        return guest;
    }

    private void validateDepartments(DeptBattle deptBattle, Member guest) {
        if (Objects.equals(deptBattle.getHostDept(), guest.getDeptId())) {
            throw new CustomException(CustomExceptionCode.SAME_DEPARTMENT);
        }
    }

    private void updateDeptBattleWithGuestInfo(DeptBattle deptBattle, Member guest, DeptGuestLeaderAttendRequest request) {
        Department department = departmentRepository.findById(guest.getDeptId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_DEPARTMENT));

        deptBattle.setGuestLeader(guest.getMemberIdx());
        deptBattle.setGuestDept(guest.getDeptId());
        deptBattle.setGuestDeptName(department.getDeptName());
        deptBattle.setMatchStatus(MatchStatus.WAITING);
        deptBattle.setInvitationCode(generateRandomString(8));

        deptBattleRepository.save(deptBattle);
    }

    private void manageParticipants(DeptBattle deptBattle, Member guest) {
        int totalParticipant = participantRepository.countByDeptBattleId(deptBattle.getDeptBattleId());
        // 마지막 참가자일 경우 대기중으로 변경.
        if (totalParticipant == deptBattle.getTeamPtcLimit() * 2 - 1) {
            deptBattle.setMatchStatus(MatchStatus.PREPARED);
            deptBattleRepository.save(deptBattle);
        }

        Participant participant = Participant.builder()
                .memberIdx(guest.getMemberIdx())
                .nickName(guest.getNickname())
                .userName(guest.getName())
                .deptBattleId(deptBattle.getDeptBattleId())
                .univId(deptBattle.getUnivId())
                .build();
        participantRepository.save(participant);
    }

    private void manageChatRoomMembers(DeptBattle deptBattle, Member guest) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(1, deptBattle.getDeptBattleId());
        if (chatRoom == null) {
            // Handle chat room not found
            return;
        }

        Member host = memberRepository.findById(deptBattle.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .customChatRoomName(deptBattle.getHostDeptName() + " 대항전")
                .memberIdx(guest.getMemberIdx())
                .chatRoomImg(host.getUnivLogoImg())
                .build();
        chatMemberRepository.save(chatMember);

        ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(deptBattle.getHostLeader(), chatRoom.getChatRoomId());
        if (hostChatMember != null) {
            hostChatMember.setCustomChatRoomName(deptBattle.getGuestDeptName() + " 대항전");
            hostChatMember.setChatRoomImg(guest.getUnivLogoImg());
            chatMemberRepository.save(hostChatMember);
        }
    }


    /**
     * 경기 종료 메서드
     * @author 이시영
     * @param deptBattleId
     */
    // 경기 종료 처리 메서드
    private void checkIncompleteMatch(Long deptBattleId) {
        DeptBattle deptBattle = deptBattleRepository.findById(deptBattleId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 경기 상태가 완료되지 않았을 경우 경기를 종료 처리합니다.
        if (deptBattle.getMatchStatus() != MatchStatus.COMPLETED) {
            deptBattle.setMatchStatus(MatchStatus.COMPLETED);
            deptBattle.setEndDt(LocalDateTime.now());
            deptBattleRepository.save(deptBattle);

            // 승리 팀 업데이트
            int winExistence = rankMapper.checkDeptExistence(deptBattle.getUnivId(), deptBattle.getWinDept(), deptBattle.getEventId());
            if (winExistence == 0) {
                rankMapper.insertDeptRank(deptBattle.getUnivId(), deptBattle.getWinDept(), deptBattle.getEventId(), 10L, 1L, 0L);
            } else {
                rankMapper.updateDeptWinRank(deptBattle.getUnivId(),deptBattle.getWinDept() ,deptBattle.getEventId());
            }

            // 패배 팀 업데이트
            int loseExistence = rankMapper.checkDeptExistence(deptBattle.getUnivId(), deptBattle.getLoseDept(), deptBattle.getEventId());
            if (loseExistence == 0) {
                rankMapper.insertDeptRank(deptBattle.getUnivId(), deptBattle.getLoseDept(), deptBattle.getEventId(), 0L, 0L, 1L);
            } else {
                rankMapper.updateDeptLoseRank(deptBattle.getUnivId(), deptBattle.getLoseDept(), deptBattle.getEventId());
            }


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