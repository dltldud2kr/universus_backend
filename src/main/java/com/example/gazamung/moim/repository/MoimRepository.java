package com.example.gazamung.moim.repository;


import com.example.gazamung.moim.entity.Moim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoimRepository extends JpaRepository<Moim, Long> {

    List<Moim> findByMoimId(Long moimId);
}
