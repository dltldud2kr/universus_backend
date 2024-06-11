package com.example.gazamung.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MemberMapper {

    // @Delete("DELETE FROM club_member WHERE member_idx = #{memberIdx}")
    // void deleteClubMembersByMemberId(Long memberIdx);

    // @Delete("DELETE FROM chat_member WHERE member_idx = #{memberIdx}")
    // void deleteChatMembersByMemberId(Long memberIdx);

    // @Delete("DELETE FROM member WHERE member_idx = #{memberIdx}")
    // void deleteMemberById(Long memberIdx);

    void deleteClubMembersByMemberId(Long memberIdx);
    void deleteChatMembersByMemberId(Long memberIdx);
    void deleteMemberById(Long memberIdx);

}
