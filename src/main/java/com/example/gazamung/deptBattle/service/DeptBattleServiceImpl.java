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

        Member member = memberRepository.findById(request.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        long univId = member.getUnivId();


        University university = universityRepository.findById(univId)
                .orElseThrow(() ->new CustomException(CustomExceptionCode.NOT_FOUND));

        Department department = departmentRepository.findById(member.getDeptId())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_DEPARTMENT));

        DeptBattle deptBattle = DeptBattle.builder()
                .hostLeader(request.getHostLeader())
                .eventId(request.getEventId())
                .univId(univId)
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

        DeptBattle result = deptBattleRepository.save(deptBattle);

        //대항전 참가자 테이블에 생성자 추가
        Participant participant = Participant.builder()
                .memberIdx(member.getMemberIdx())
                .deptBattleId(result.getDeptBattleId())
                .userName(member.getName())
                .nickName(member.getNickname())
                .univId(univId)
                .build();
        participantRepository.save(participant);

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomType(1)
                .dynamicId(result.getDeptBattleId())
                .chatRoomName(university.getSchoolName() + "대항전")
                .build();

        chatRoomRepository.save(chatRoom);

        Optional<ChatMember> findChatMember = chatMemberRepository.findByMemberIdxAndChatRoomIdAndChatRoomType(member.getMemberIdx(),chatRoom.getChatRoomId(),1);
        if(findChatMember.isEmpty()){
            ChatMember chatMember = ChatMember.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .chatRoomType(1)
                    .memberIdx(member.getMemberIdx())
                    .chatRoomName(chatRoom.getChatRoomName())
                    .build();

            chatMemberRepository.save(chatMember);
        }


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
        int totalParticipant = participantRepository.countByUnivBattleId(deptBattle.getDeptBattleId());

        // 마지막 참가자일 경우 대기중으로 변경.
        if(totalParticipant == deptBattle.getTeamPtcLimit() - 1){
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

        return true;
    }




    @Override
    public boolean attend(DeptBattleAttendRequest request) {
        return false;
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
