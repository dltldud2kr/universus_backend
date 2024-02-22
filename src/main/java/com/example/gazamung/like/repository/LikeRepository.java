package com.example.gazamung.like.repository;

import com.example.gazamung.like.entity.Like;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.moim.entity.Moim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberAndMoim(Member member, Moim moim);
}
