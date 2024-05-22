package ru.effectivemobile.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.testtask.model.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
}
