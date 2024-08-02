package ru.effectivemobile.testtask.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.Email;
import ru.effectivemobile.testtask.model.PhoneNumber;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUsername(String username);

    @Query("""
            SELECT c FROM Client c WHERE
            (CAST(:birthdate AS TIMESTAMP) IS NULL OR c.birthdate>=:birthdate ) AND
            (:fullName IS NULL OR :fullName='' OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND
            (:phoneNumber IS NULL OR :phoneNumber MEMBER OF c.phoneNumbers) AND
            (:email IS NULL OR :email MEMBER OF c.emails)""")
    Page<Client> findByParams(@Param("birthdate") Date birthdate,
                              @Param("fullName") String fullName,
                              @Param("phoneNumber") PhoneNumber phoneNumber,
                              @Param("email") Email email,
                              Pageable pageable);

    @Query("""
            SELECT c FROM Client c WHERE
            (:username IS NULL OR :username='' OR c.username=:username) AND
            (:phoneNumber IS NULL OR :phoneNumber MEMBER OF c.phoneNumbers) AND
            (:email IS NULL OR :email MEMBER OF c.emails)""")
    Client findByParamsForMoneyTransfer(@Param("username") String username,
                                        @Param("phoneNumber") PhoneNumber phoneNumber,
                                        @Param("email") Email email);
}
