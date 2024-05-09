package com.example.gazamung.reply.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung._enum.MsgType;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.fcmSend.FcmSendDto;
import com.example.gazamung.fcmSend.FcmService;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.service.NotificationService;
import com.example.gazamung.participant.entity.Participant;
import com.example.gazamung.reply.dto.CreateDto;
import com.example.gazamung.reply.dto.InfoDto;
import com.example.gazamung.reply.dto.ModifyDto;
import com.example.gazamung.reply.entity.Reply;
import com.example.gazamung.reply.repository.ReplyRepository;
import com.example.gazamung.univBoard.entity.UnivBoard;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final UnivBoardRepository univBoardRepository;
    private final NotificationService notificationService;
    private final FcmService fcmService;

    @Override
    public Object createReply(CreateDto dto) {
        // 사용자와 게시글 존재 여부를 확인
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        UnivBoard univBoard = univBoardRepository.findById(dto.getUnivBoardId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

        // 댓글 생성
        Reply reply = Reply.builder()
                .memberIdx(dto.getMemberIdx())
                .univBoardId(dto.getUnivBoardId())
                .lastDt(LocalDateTime.now())
                .content(dto.getContent())
                .build();

        replyRepository.save(reply);

        // 게시글 작성자에게 알림 보내기
        if (!member.getMemberIdx().equals(univBoard.getMemberIdx())) { // 자신의 게시글에 댓글을 달 경우 알림을 보내지 않음
            FcmSendDto fcmSendDto = FcmSendDto.builder()
                    .token("dWVpAXGoS0-qW8txlowMKt:APA91bEUdfKJYNQYLTDppQVhwQtXoUfwhgYLnTEgoLhZmTXfY8YbK" +
                            "HeAhiTDoMxXHChr2mhb-eA3eNb0MPUpAHHwceXciW4FZhck-AfWSbHQmwkTHRljIuTFZAhhDYDRKqF2WIZMnpYL")
                    .title("새로운 댓글이 달렸습니다")
                    .body(member.getNickname() + "님이 댓글을 작성하셨습니다")
                    .build();
            try {
                fcmService.sendMessageTo(fcmSendDto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



        return true;
    }

    @Override
    public List<InfoDto> listReply(Long univBoardId) {

        univBoardRepository.findById(univBoardId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

        List<Reply> replys = replyRepository.findByUnivBoardId(univBoardId);

        List<InfoDto> infoDtoList = new ArrayList<>();

        if (replys.isEmpty()){
            return null;
        }

        for (Reply reply : replys){
            Member member = memberRepository.findById(reply.getMemberIdx())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            InfoDto infoDto = InfoDto.builder()
                    .nickname(member.getNickname())
                    .profileImgUrl(member.getProfileImgUrl())
                    .content(reply.getContent())
                    .lastDt(reply.getLastDt())
                    .build();

            infoDtoList.add(infoDto);
        }

        return infoDtoList;
    }

    @Override
    public Object deleteReply(Long replyId, Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_REPLY));

        if ((reply.getMemberIdx().equals(memberIdx)) || member.getRole() == 1){
            replyRepository.delete(reply);
        }

        return true;

    }

    @Override
    public Object modifyReply(ModifyDto dto) {
        Reply reply = replyRepository.findById(dto.getReplyId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_REPLY));

        memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        if (reply.getMemberIdx().equals(dto.getMemberIdx())){
            reply.setLastDt(dto.getLastDt());
            reply.setContent(dto.getContent());

            replyRepository.save(reply);
        }

        return true;
    }
}
