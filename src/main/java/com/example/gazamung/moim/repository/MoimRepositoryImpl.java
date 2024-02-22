package com.example.gazamung.moim.repository;

import com.example.gazamung.moim.entity.Moim;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class MoimRepositoryImpl implements MoimCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addLikeCount(Moim moim) {
//        moim.setLikeCount(moim.getLikeCount() + 1);
        moim.incrementLikeCount();
        entityManager.merge(moim);
    }

    @Override
    @Transactional
    public void deleteLikeCount(Moim moim) {
        moim.decrementLikeCount();
        entityManager.merge(moim);
    }
}
