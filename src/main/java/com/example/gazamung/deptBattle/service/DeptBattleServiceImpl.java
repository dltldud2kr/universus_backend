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
import com.example.gazamung.univBattle.dto.GuestLeaderAttendRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.service.UnivBattleServiceImpl;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

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
    private final UnivBattleServiceImpl univBattleService;


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

        DeptBattle deptBattle = deptBattleRepository.findById(request.getDeptBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        //이미 참가팀 대표가 있는지 확인
        if (deptBattle.getGuestLeader() != null){
            throw new CustomException(CustomExceptionCode.REPRESENTATIVE_ALREADY_EXISTS);
        }

        // 참가자 정보 조회
        Member guest = memberRepository.findById(request.getGuestLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 같은 대학교인지 확인
        if (!Objects.equals(deptBattle.getUnivId(), guest.getUnivId())){
            throw new CustomException(CustomExceptionCode.NOT_SAME_UNIVERSITY);
        }

        // 주최자 대학교
        long hostDept = deptBattle.getHostDept();
        // 참가자 대학교
        long guestDept = guest.getDeptId();

        Department department = departmentRepository.findById(guest.getDeptId()).
                orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_DEPARTMENT));

        String guestDeptName = department.getDeptName();

        // 같은 과는 참가 불가능.
        if (hostDept == guestDept) {
            throw new CustomException(CustomExceptionCode.SAME_DEPARTMENT);
        }

        // Guest 팀 정보 업데이트
        deptBattle.setGuestLeader(request.getGuestLeader());
        deptBattle.setGuestDept(guestDept);
        deptBattle.setGuestDeptName(guestDeptName);

        // 상태를 "대기중" 으로 바꿈
        deptBattle.setMatchStatus(MatchStatus.WAITING);

        // 초대 코드 생성
        deptBattle.setInvitationCode(generateRandomString(8));

        deptBattleRepository.save(deptBattle);

        // 참가인원 초과 여부 체크
        int totalParticipant = participantRepository.countByDeptBattleId(deptBattle.getDeptBattleId());

        // 마지막 참가자일 경우 대기중으로 변경.
        if(totalParticipant == deptBattle.getTeamPtcLimit() * 2 - 1){
            deptBattle.setMatchStatus(MatchStatus.PREPARED);
        }

        Participant participant = Participant.builder()
                .memberIdx(guest.getMemberIdx())
                .nickName(guest.getNickname())
                .userName(guest.getName())
                .deptBattleId(request.getDeptBattleId())
                .univId(deptBattle.getUnivId())
                .build();

        participantRepository.save(participant);

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomTypeAndDynamicId(1,deptBattle.getDeptBattleId());

        ChatMember chatMember = ChatMember.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomType(1)
                .customChatRoomName(deptBattle.getHostDeptName() + "대항전")
                .memberIdx(guest.getMemberIdx())
                .build();

        chatMemberRepository.save(chatMember);

        ChatMember hostChatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(deptBattle.getHostLeader(),chatRoom.getChatRoomId());

        hostChatMember.setCustomChatRoomName(deptBattle.getGuestDeptName() + "대항전");

        chatMemberRepository.save(hostChatMember);

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
        int univTotalParticipant = participantRepository.countByDeptBattleIdAndUnivId(deptBattle.getDeptBattleId(), member.getUnivId());
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
                .univBattleId(request.getDeptBattleId())
                .univId(member.getUnivId())
                .userName(member.getName())
                .nickName(member.getNickname())
                .build();
        participantRepository.save(participant);

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
