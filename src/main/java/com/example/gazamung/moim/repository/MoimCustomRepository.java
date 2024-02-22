package com.example.gazamung.moim.repository;

import com.example.gazamung.moim.entity.Moim;

public interface MoimCustomRepository {

    void addLikeCount(Moim moim);
    void deleteLikeCount(Moim moim);
}
