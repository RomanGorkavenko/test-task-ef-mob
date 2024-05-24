package ru.effectivemobile.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.testtask.model.Email;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    Optional<Email> findByEmail(String email);

    Optional<Email> findByEmailAndClientId(String email, Long clientId);
}
