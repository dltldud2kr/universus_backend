package com.example.gazamung.like.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.like.dto.LikeRequestDto;
import com.example.gazamung.like.entity.Like;
import com.example.gazamung.like.repository.LikeRepository;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.moim.entity.Moim;
import com.example.gazamung.moim.repository.MoimRepository;
import com.example.gazamung.moim.service.MoimServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final MoimRepository moimRepository;
    private final MemberRepository memberRepository;
    private final MoimServiceImpl moimServiceImpl;

    @Override
    public boolean addLike(LikeRequestDto likeRequestDto) {

        Moim moim = moimRepository.findById(likeRequestDto.getMoimId())
                .orElseThrow(()-> new NotFoundException("해당 모임을 찾을 수 없습니다."));

        Member member = memberRepository.findById(likeRequestDto.getMemberIdx())
                .orElseThrow(()-> new NotFoundException("해당 사용자를 찾을 수 없습니다."));

       if (likeRepository.findByMemberAndMoim(member, moim).isPresent()){
           throw new CustomException(CustomExceptionCode.NOT_FOUND);
       }

        Like like = Like.builder()
                .moim(moim)
                .member(member)
                .build();

       likeRepository.save(like);
       moimRepository.addLikeCount(moim);

        return true;
    }

    @Override
    public boolean deleteLike(LikeRequestDto likeRequestDto) {

        Moim moim = moimRepository.findById(likeRequestDto.getMoimId())
                .orElseThrow(()-> new NotFoundException("해당 모임을 찾을 수 없습니다."));

        Member member = memberRepository.findById(likeRequestDto.getMemberIdx())
                .orElseThrow(()-> new NotFoundException("해당 사용자를 찾을 수 없습니다."));

        Like like = likeRepository.findByMemberAndMoim(member, moim)
                .orElseThrow(() -> new NotFoundException("해당 사용자의 좋아요를 찾을 수 없습니다."));

        likeRepository.delete(like);
        moimRepository.deleteLikeCount(moim);
        return true;

    }
}
