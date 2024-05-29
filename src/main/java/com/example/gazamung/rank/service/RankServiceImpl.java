package com.example.gazamung.rank.service;

import com.example.gazamung.mapper.RankMapper;
import com.example.gazamung.rank.dto.UnivRankRes;
import com.example.gazamung.rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final RankRepository rankRepository;
    private final RankMapper rankMapper;

    @Override
    public List<UnivRankRes> rankList(Long eventId) {
        if (eventId != null) {
            return rankMapper.findUnivRanksByEventId(eventId);
        } else {
            return rankMapper.findAllUnivRanks();
        }

    }
}