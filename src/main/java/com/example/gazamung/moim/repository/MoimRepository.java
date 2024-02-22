package com.example.gazamung.moim.repository;


import com.example.gazamung.moim.entity.Moim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MoimRepository extends JpaRepository<Moim, Long>, MoimCustomRepository{

    List<Moim> findByMoimId(Long moimId);

    List<Moim> findAllByCategoryId(Long categoryId);

    @Query("SELECT m FROM Moim m ORDER BY m.likeCnt DESC")
    List<Moim> findAllOrderByLikeCntDesc();
    
}
