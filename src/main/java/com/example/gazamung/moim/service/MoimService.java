package com.example.gazamung.moim.service;

import com.example.gazamung.membership.dto.MembershipDto;
import com.example.gazamung.moim.controller.MoimController;
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

    List<MoimDto> search(String keyword, MoimController.SearchField searchField);

    boolean joinRequest(Long moimId, Long memberIdx);

    void approveJoinRequest(Long memberIdx, Long notificationId);

    void rejectJoinRequest(Long memberIdx, Long notifiactionId);

}
