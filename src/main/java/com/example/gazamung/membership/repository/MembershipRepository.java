package com.example.gazamung.membership.repository;

import com.example.gazamung.membership.entity.Membership;
import com.example.gazamung.moim.service.MoimServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    long countByMoimIdAndStatus(Long moimId, MoimServiceImpl.MembershipStatus membershipStatus);


    Membership findByMemberIdx(Long memberIdx);
}
