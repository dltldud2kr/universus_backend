package com.example.gazamung.reply.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.reply.dto.CreateDto;
import com.example.gazamung.reply.dto.InfoDto;
import com.example.gazamung.reply.dto.ModifyDto;
import com.example.gazamung.reply.entity.Reply;
import com.example.gazamung.reply.repository.ReplyRepository;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public Object createReply(CreateDto dto) {
        memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        univBoardRepository.findById(dto.getUnivBoardId())
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

        Reply reply = Reply.builder()
                .memberIdx(dto.getMemberIdx())
                .univBoardId(dto.getUnivBoardId())
                .lastDt(LocalDateTime.now())
                .content(dto.getContent())
                .build();

        replyRepository.save(reply);

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
