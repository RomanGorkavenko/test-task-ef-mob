package ru.effectivemobile.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.testtask.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
