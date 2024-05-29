package com.example.gazamung.clubMember.repository;

import com.example.gazamung.clubMember.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {


    Optional<ClubMember> findClubMemberByMemberIdx(Long memberIdx);



    Long countByClubId(Long clubId);

    Optional<ClubMember> findByClubIdAndMemberIdx(long clubId, long memberIdx);

    List<ClubMember> findByMemberIdx(Long memberIdx);

    List<ClubMember> findAllByClubId(Long clubId);

    boolean existsByClubIdAndMemberIdx(Long clubId, Long memberIdx);
}
