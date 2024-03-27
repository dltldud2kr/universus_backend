package com.example.gazamung.clubMember.repository;

import com.example.gazamung.clubMember.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {


    Optional<ClubMember> findClubMemberByMemberIdx(Long memberIdx);



    Long countByClubId(Long clubId);

    Optional<ClubMember> findByClubIdAndMemberIdx(long clubId, long memberIdx);
}
