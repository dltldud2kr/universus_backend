package com.example.gazamung.univBoard.repository;

import com.example.gazamung.univBoard.entity.UnivBoard;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnivBoardRepository extends JpaRepository<UnivBoard, Long> {
    List<UnivBoard> findByUnivId(Long univId);

    List<UnivBoard> findByTitleContaining(String query);

//    List<UnivBoard> findByClubIdIsNull();

//    List<UnivBoard> findByClubId(Long clubId);


    List<UnivBoard> findByCategoryIdAndUnivId(Long i, Long univId, Sort regDt);

    List<UnivBoard> findByClubIdIsNullAndUnivId(Long univId);

    List<UnivBoard> findByClubIdAndUnivId(Long clubId, Long univId);


    String findEventNameByEventId(Long eventId);
}
