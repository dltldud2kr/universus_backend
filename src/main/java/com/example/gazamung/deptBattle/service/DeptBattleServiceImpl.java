package com.example.gazamung.deptBattle.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.MatchStatus;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomRepository;
import com.example.gazamung.deptBattle.dto.DeptBattleAttendRequest;
import com.example.gazamung.deptBattle.dto.DeptBattleCreateRequest;
import com.example.gazamung.deptBattle.entity.DeptBattle;
import com.example.gazamung.deptBattle.repository.DeptBattleRepository;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.participant.entity.Participant;
import com.example.gazamung.participant.repository.ParticipantRepository;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.university.entity.University;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public boolean create(DeptBattleCreateRequest request) {

        Member member = memberRepository.findById(request.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        long univId = member.getUnivId();


        University university = universityRepository.findById(univId)
                .orElseThrow(() ->new CustomException(CustomExceptionCode.NOT_FOUND));

        DeptBattle deptBattle = DeptBattle.builder()
                .hostLeader(request.getHostLeader())
                .eventId(request.getEventId())
                .univId(univId)
                .hostDept(request.getHostDept())
                .content(request.getContent())
                .cost(request.getCost())
                .battleDate(request.getBattleDate())
                .location(request.getLocation())
                .matchStatus(MatchStatus.RECRUIT)
                .regDt(LocalDateTime.now())
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
    public boolean attend(DeptBattleAttendRequest request) {
        return false;
    }
}
