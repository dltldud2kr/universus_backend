package com.example.gazamung.search;

import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.repository.ClubRepository;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final ClubRepository clubRepository;
    private final UnivBoardRepository univBoardRepository;

    public List<?> searchResult(int category, String query){

        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        if (category == 0) {
            // 카테고리 0일 경우 클럽 검색
            return clubRepository.findByClubNameContaining(query);  // 이름에 query 가 포함된 클럽 검색
        } else if (category == 1){
            // 카테고리 1일 경우 대학 게시판 검색
            return univBoardRepository.findByTitleContaining(query);
        }

        return Collections.emptyList(); // 적합한 카테고리가 없을 경우 빈 리스트 반환
    }

}
