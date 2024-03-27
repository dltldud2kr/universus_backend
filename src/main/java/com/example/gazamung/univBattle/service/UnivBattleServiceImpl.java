package com.example.gazamung.univBattle.service;

import com.example.gazamung.ChatRoom.ChatRoom;
import com.example.gazamung.ChatRoom.ChatRoomRepository;
import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.univBattle.dto.UnivBattleAttendRequest;
import com.example.gazamung.univBattle.dto.UnivBattleCreateRequest;
import com.example.gazamung.univBattle.entity.UnivBattle;
import com.example.gazamung.univBattle.repository.UnivBattleRepository;
import com.example.gazamung.university.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnivBattleServiceImpl implements UnivBattleService {

    private final UniversityRepository universityRepository;
    private final UnivBattleRepository univBattleRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    /**
     * 대항전 생성
     * @param request
     * @return
     */
    @Override
    public boolean create(UnivBattleCreateRequest request) {

        Member member = memberRepository.findById(request.getHostLeader())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        long univId = member.getUnivId();

        // 대항전 생성
        UnivBattle univBattle = UnivBattle.builder()
                .hostLeader(request.getHostLeader())
                .eventId(request.getEventId())
                .hostUniv(univId)
                .battleDate(request.getBattleDate())
                .location(request.getLocation())
                .content(request.getContent())
                .status(0)
                .cost(request.getCost())
                .regDt(LocalDateTime.now())
                .build();

        univBattleRepository.save(univBattle);

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomType(0)
                .univBattleId(univBattle.getUnivBattleId())
                .build();

        chatRoomRepository.save(chatRoom);

        return true;

    }

    /**
     *  대항전 참가
     * @param request
     * @return
     */
    @Override
    public boolean attend(UnivBattleAttendRequest request) {

        UnivBattle univBattle = univBattleRepository.findById(request.getUnivBattleId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));

        // 참가자 정보 조회
        Member guest = memberRepository.findById(request.getGuestLeader())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        // 주최자 대학교
        long hostUniv = univBattle.getHostUniv();
        // 참가자 대학교
        long guestUniv = guest.getUnivId();

        // 같은 학교는 참가 불가능.
        if (hostUniv == guestUniv) {
            throw new CustomException(CustomExceptionCode.SAME_UNIVERSITY);
        }

        univBattle.setGuestLeader(request.getGuestLeader());
        univBattle.setGuestUniv(guestUniv);
        // 초대코드 생성
        univBattle.setInvitationCode(generateRandomString(8));

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
}
