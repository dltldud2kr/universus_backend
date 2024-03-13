package com.example.gazamung.mapper;

import com.example.gazamung.club.dto.ClubJoinRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClubMapper {

    int checkAgeAndMembership(@Param("memberIdx") long memberIdx, @Param("clubId") long clubId, @Param("age") int age);


    void insertClubMember(ClubJoinRequest clubJoinRequest);
}
