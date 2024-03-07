package com.example.gazamung.group.service;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import com.example.gazamung.group.dto.GroupDto;
import com.example.gazamung.group.entity.Group;
import com.example.gazamung.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public boolean create(GroupDto dto) {
        Member member = memberRepository.findById(dto.getMemberIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));


        Group group = Group.builder()
                .memberIdx(member.getMemberIdx())
                .title(dto.getTitle())
                .content(dto.getContent())
                .location(dto.getLocation())
                .categoryId(dto.getCategoryId())
                .regDt(LocalDateTime.now())
                .build();

        groupRepository.save(group);
        return true;
    }




    public boolean delete(Long groupId, Long memberIdx) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            //모임장과 삭제 요청 회원이 동일한지 확인.
            if (group.getMemberIdx().equals(memberIdx)) {
                groupRepository.delete(group);
                return true;
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        }
        throw new CustomException(CustomExceptionCode.NOT_FOUND);

    }





}

