package ru.effectivemobile.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.testtask.model.PhoneNumber;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
}
