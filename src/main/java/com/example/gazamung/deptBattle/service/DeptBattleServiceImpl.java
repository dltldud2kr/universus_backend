package com.example.gazamung.deptBattle.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.MatchStatus;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomRepository;
import com.example.gazamung.department.entity.Department;
import com.example.gazamung.department.repository.DepartmentRepository;
import com.example.gazamung.deptBattle.dto.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.dto.DeptBattleCreateRequest;
import com.example.gazamung.deptBattle.dto.DeptGuestLeaderAttendRequest;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.deptBattle.repository.DeptBattleRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.participant.entity.Participant;
import com.example.gazamung.participant.repository.ParticipantRepository;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.service.UnivBattleServiceImpl;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

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
    DeptBattle deptBattle = validateDeptBattle(request.getDeptBattleId());

    // 게스트 리더의 유효성을 검사 후 조건에 맞지 않으면 예외 발생
    Member guest = validateGuest(request.getGuestLeader(), deptBattle);

    // 동일 대학교 내의 다른 과 검증 후, 같은 과일 경우 예외를 발생
    validateDepartments(deptBattle, guest);

    // 대항전 게스트 리더 관련 정보를 업데이트
    updateDeptBattleWithGuestInfo(deptBattle, guest, request);

    // 참가자 관리 로직을 처리. 참가자 수에 따라 대항전의 상태를 업데이트.
    manageParticipants(deptBattle, guest);

    // 관련 채팅방 멤버 정보를 관리. 채팅방 이름 업데이트 포함.
    manageChatRoomMembers(deptBattle, guest);


    return true;
}

    @Override
    public List<DeptBattle> list(int status) {

        return switch (status) {
            case 0 -> deptBattleRepository.findAll();
            case 1 -> deptBattleRepository.findByMatchStatus(MatchStatus.RECRUIT);
            case 2 -> deptBattleRepository.findByMatchStatus(MatchStatus.WAITING);
            case 3 -> deptBattleRepository.findByMatchStatus(MatchStatus.IN_PROGRESS);
            case 4 -> deptBattleRepository.findByMatchStatus(MatchStatus.COMPLETED);
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
        int ptcCount = participantRepository.countByUnivBattleId(deptBattleId);
        if(ptcCount != deptBattle.getTeamPtcLimit()){
            throw new CustomException(CustomExceptionCode.INSUFFICIENT_MATCH_PLAYERS);
        }

        deptBattle.setMatchStatus(MatchStatus.IN_PROGRESS);
        deptBattle.setMatchStartDt(LocalDateTime.now());

        deptBattleRepository.save(deptBattle);

        return true;
    }


    @Override
    public boolean attend(DeptBattleAttendRequest request) {

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
        }

        // 참가자 저장
        Participant participant = Participant.builder()
                .memberIdx(request.getMemberIdx())
                .deptBattleId(request.getDeptBattleId())
                .univId(member.getUnivId())
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
                .location(request.getLocation())
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

        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .customChatRoomName(deptBattle.getHostDeptName() + " 대항전")
                .memberIdx(guest.getMemberIdx())
                .build();
        chatMemberRepository.save(chatMember);

        ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(deptBattle.getHostLeader(), chatRoom.getChatRoomId());
        if (hostChatMember != null) {
            hostChatMember.setCustomChatRoomName(deptBattle.getGuestDeptName() + " 대항전");
            chatMemberRepository.save(hostChatMember);
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
