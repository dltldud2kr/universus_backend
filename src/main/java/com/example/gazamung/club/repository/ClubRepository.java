package com.example.gazamung.club.repository;


import com.example.gazamung.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long>{

    List<Club> findAllByEventIdIn(List<Long> eventIds);

    List<Club> findByClubNameContaining(String query);

    Club findByEventId(Long eventId);

    List<Club> findByMemberIdx(Long memberIdx);

    List<Club> findAllByEventIdInAndClubIdNotIn(List<Long> eventIds, List<Long> clubIds);

    List<Club> findAllByUnivId(Long univId);

    List<Club> findByUnivId(Long univId);


    List<Club> findByclubIdIn(List<Long> clubIds);
}
