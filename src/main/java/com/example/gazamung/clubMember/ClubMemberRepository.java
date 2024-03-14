package com.example.gazamung.clubMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {


    Optional<ClubMember> findClubMemberByMemberIdx(Long memberIdx);


}
