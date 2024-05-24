package ru.effectivemobile.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.testtask.model.PhoneNumber;

import java.util.Optional;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    Optional<PhoneNumber> findByNumber(String number);

    Optional<PhoneNumber> findByNumberAndClientId(String number, Long clientId);
}
