package com.example.gazamung.search;

import com.example.gazamung.club.entity.Club;
import com.example.gazamung.club.repository.ClubRepository;
import com.example.gazamung.event.entity.Event;
import com.example.gazamung.event.repository.EventRepository;
import com.example.gazamung.univBoard.repository.UnivBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final ClubRepository clubRepository;
    private final UnivBoardRepository univBoardRepository;
    private final EventRepository eventRepository;

    public List<?> searchResult(int category, String query){

        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        if (category == 0) {
            // 카테고리 0일 경우 클럽 검색
            List<Club> clubsByName = clubRepository.findByClubNameContaining(query);
            List<Event> eventsByName = eventRepository.findByEventNameContaining(query);

            // 이벤트 이름으로 찾은 클럽 리스트
            List<Club> clubsByEvent = new ArrayList<>();
            for (Event event : eventsByName) {
                List<Club> clubs = clubRepository.findByEventId(event.getEventId());
                clubsByEvent.addAll(clubs);
            }

            // 두 리스트를 합치고 중복 제거
            Set<Club> resultSet = new HashSet<>(clubsByName);
            resultSet.addAll(clubsByEvent);

            return new ArrayList<>(resultSet);

        } else if (category == 1){
            // 카테고리 1일 경우 대학 게시판 검색
            return univBoardRepository.findByTitleContaining(query);
        }

        return Collections.emptyList(); // 적합한 카테고리가 없을 경우 빈 리스트 반환
    }

}
