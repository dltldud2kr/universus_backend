package com.example.gazamung.moim.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.moim.dto.MoimDto;
import com.example.gazamung.moim.entity.Moim;
import com.example.gazamung.moim.repository.MoimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoimServiceImpl implements MoimService {

    private final MoimRepository moimRepository;
    private final MemberRepository memberRepository;


    public boolean create(MoimDto dto){
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        Moim moim = Moim.builder()
                .memberIdx(member.getMemberIdx())
                .title(dto.getTitle())
                .content(dto.getContent())
                .location(dto.getLocation())
                .regDt(LocalDateTime.now())
                .build();

        moimRepository.save(moim);
        return true;
    }


    private List<MoimDto> MoimDtoList(List<Moim> list) {
        return list.stream()
                .map(moim -> {
                    return MoimDto.builder()
                            .moimId(moim.getMoimId())
                            .memberIdx(moim.getMemberIdx())
                            .title(moim.getTitle())
                            .content(moim.getContent())
                            .location(moim.getLocation())
                            .regDt(moim.getRegDt())
                            .build();
                })
                .collect(Collectors.toList());
    }
    public List<MoimDto> info(Long moimId){
        List<Moim> list = moimRepository.findByMoimId(moimId);
        return MoimDtoList(list);
    }

    public boolean delete(Long moimId, Long memberIdx) {
        Optional<Moim> moimOpt = moimRepository.findById(moimId);

        if (moimOpt.isPresent()){
            Moim moim = moimOpt.get();

            if(moim.getMemberIdx().equals(memberIdx)){
                moimRepository.delete(moim);
                return true;
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        } throw new CustomException(CustomExceptionCode.NOT_FOUND);

    }

    public boolean update(Long moimId, Long memberIdx, MoimDto dto) {
        Moim moim = moimRepository.findById(moimId)
                .filter(m -> m.getMemberIdx().equals(memberIdx))
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        moim.setTitle(dto.getTitle());
        moim.setContent(dto.getContent());
        moim.setLocation(dto.getLocation());
        moimRepository.save(moim);
        return true;
    }


}
