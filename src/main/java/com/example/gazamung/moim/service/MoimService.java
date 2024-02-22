package com.example.gazamung.moim.service;

import com.example.gazamung.moim.dto.MoimDto;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface MoimService {
    boolean create(MoimDto dto);

    List<MoimDto> info(Long moimId);

    boolean delete(Long moimId, Long memberIdx);

    boolean update(Long moimId, Long memberIdx, MoimDto dto);

    List<MoimDto> list();

    List<MoimDto> listCategory(Long categoryId);

    List<MoimDto> listLikeCnt();
}
