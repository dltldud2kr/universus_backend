package com.example.gazamung.like.service;

import com.example.gazamung.like.dto.LikeRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    boolean addLike(LikeRequestDto likeRequestDto);

    boolean deleteLike(LikeRequestDto likeRequestDto);
}
