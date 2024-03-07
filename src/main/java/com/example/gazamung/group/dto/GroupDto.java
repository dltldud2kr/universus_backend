package com.example.gazamung.group.dto;

import com.example.gazamung.group.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupDto {

    private Long groupId;

    private Long memberIdx;
    private String title;
    private String content;
    private String location;
    private Long categoryId;

    private LocalDateTime regDt;    // 생성일

    private Long maximumParticipants;   // 총 인원
    private Long currentParticipants;   // 현재 인원

    private Long bookmarkCnt;

    public static GroupDto convertToDto(Group group) {
        return GroupDto.builder()
                .groupId(group.getGroupId())
                .memberIdx(group.getMemberIdx())
                .title(group.getTitle())
                .content(group.getContent())
                .location(group.getLocation())
                .categoryId(group.getCategoryId())
                .regDt(group.getRegDt())
                .bookmarkCnt(group.getBookmarkCnt())
                .build();
    }

}
