package com.example.gazamung.university;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {

    Optional<University> findBySchoolName(String schoolName);
}
