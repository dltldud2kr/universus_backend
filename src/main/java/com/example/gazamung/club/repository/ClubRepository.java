package com.example.gazamung.club.repository;


import com.example.gazamung.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long>{

    List<Club> findAllByEventIdIn(List<Long> eventIds);

    List<Club> findByClubNameContaining(String query);

//    Club findByEventId(Long eventId);

    List<Club> findByMemberIdx(Long memberIdx);


    List<Club> findAllByEventIdInAndClubIdNotInAndUnivId(List<Long> eventIds, List<Long> clubIds, Long univId);

    List<Club> findAllByUnivId(Long univId);

    List<Club> findByUnivId(Long univId);


    List<Club> findByclubIdIn(List<Long> clubIds);

    List<Club> findByEventId(Long eventId);

    List<Club> findAllByClubIdNotInAndUnivId(List<Long> suggestedClubIds, Long univId);

    List<Club> findAllByEventIdInAndClubIdInAndUnivId(List<Long> eventIds, List<Long> clubIds, Long univId);
}
