package com.example.gazamung.club.repository;


import com.example.gazamung.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long>{

    List<Club> findAllByEventIdIn(List<Long> eventIds);

    List<Club> findByClubNameContaining(String query);

    Club findByEventId(Long eventId);

    Club findByMemberIdx(Long memberIdx);
}
