package com.example.gazamung.univBoard.service;

import com.example.gazamung.univBoard.dto.UnivBoardDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UnivBoardService {
    Map<String, Object> createPost(UnivBoardDto.CreateUnivBoardDto dto);

    Object infoPost(Long univBoardId);

    List<UnivBoardDto.InfoUnivBoardDto> listUniv(Long memberIdx);
}