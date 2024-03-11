package com.example.gazamung.emailAuth.repository;//package flambus.flambus_v10.repository;


import com.example.gazamung._enum.EmailAuthStatus;
import com.example.gazamung.emailAuth.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    Optional<EmailAuth> findByEmailAndEmailAuthStatus(String email, EmailAuthStatus emailAuthStatus);

    List<EmailAuth> findListByEmailAndEmailAuthStatus(String email, EmailAuthStatus emailAuthStatus);

    Optional<EmailAuth> findByEmail(String email);

}
