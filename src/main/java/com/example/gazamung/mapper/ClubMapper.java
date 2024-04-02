package com.example.gazamung.mapper;

import com.example.gazamung.club.dto.ClubJoinRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClubMapper {

    int ageCheck( @Param("clubId") long clubId, @Param("age") int age);


    int countByMemberIdx( @Param("memberIdx") Long memberIdx);

    int checkClubMembership(@Param("clubId") Long clubId, @Param("memberIdx") Long memberIdx);



}
