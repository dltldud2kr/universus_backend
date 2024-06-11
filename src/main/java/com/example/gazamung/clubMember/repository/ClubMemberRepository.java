package com.example.gazamung.clubMember.repository;

import com.example.gazamung.clubMember.entity.ClubMember;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {


    Optional<ClubMember> findClubMemberByMemberIdx(Long memberIdx);



    Long countByClubId(Long clubId);

    Optional<ClubMember> findByClubIdAndMemberIdx(long clubId, long memberIdx);

    List<ClubMember> findByMemberIdx(Long memberIdx);

    List<ClubMember> findAllByClubId(Long clubId);

//    boolean existsByClubIdAndMemberIdx(Long clubId, Long memberIdx);

    @Query("SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END FROM ClubMember cm WHERE cm.clubId = :clubId AND cm.memberIdx = :memberIdx")
    boolean existsByClubIdAndMemberIdx(@Param("clubId") Long clubId, @Param("memberIdx") Long memberIdx);

}
