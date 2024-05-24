package ru.effectivemobile.testtask.service;

import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.exception.BalancePositiveException;
import ru.effectivemobile.testtask.exception.CustomAccessDeniedException;
import ru.effectivemobile.testtask.exception.NumberOfPhoneNumbersException;
import ru.effectivemobile.testtask.model.Account;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.Email;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.repository.ClientRepository;
import ru.effectivemobile.testtask.search.SearchClientForMoneyTransfer;
import ru.effectivemobile.testtask.web.dto.ClientRequest;
import ru.effectivemobile.testtask.web.dto.EmailUpdate;
import ru.effectivemobile.testtask.web.dto.PhoneNumberUpdate;
import ru.effectivemobile.testtask.web.security.JwtEntity;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final AccountService accountService;
    private final EmailService emailService;
    private final PhoneNumberService phoneNumberService;
    private final PasswordEncoder passwordEncoder;

    private final ReentrantLock lock = new ReentrantLock();

    @Transactional(rollbackOn = Exception.class)
    public Client create(ClientRequest clientRequest) {

        Faker faker = new Faker(new Locale("ru-RU"));

        Double balance = ChangeBalance.round(clientRequest.getBalance());

        Account account = accountService.create(balance);

        Client client = new Client();
        client.setAccount(account);
        client.setUsername(clientRequest.getUsername());
        client.setPassword(passwordEncoder.encode(clientRequest.getPassword()));
        client.setFullName(faker.name().fullName());
        client.setBirthdate(faker.date().birthday());

        Client clientResult = clientRepository.save(client);

        phoneNumberService.create(clientRequest.getPhoneNumber(), clientResult);
        emailService.create(clientRequest.getEmail(), clientResult);

        return clientResult;
    }

    public Page<Client> findByParams(Date birthdate, String fullName,
                                     String phoneNumber, String email, PageRequest paging) {

        PhoneNumber phoneNumberSearch = null;
        Email emailSearch = null;

        if (phoneNumber != null) {
            phoneNumberSearch = phoneNumberService.findByNumber(phoneNumber);
        }

        if (email != null) {
            emailSearch = emailService.findByEmail(email);
        }

        return clientRepository.findByParams(birthdate, fullName, phoneNumberSearch, emailSearch, paging);

    }

    @Transactional
    public Double moneyTransfer(SearchClientForMoneyTransfer searchClientForMoneyTransfer) {

        String username = searchClientForMoneyTransfer.getUsername() != null
                ? searchClientForMoneyTransfer.getUsername() : null;

        Long money = ChangeBalance.toLong(ChangeBalance.round(searchClientForMoneyTransfer.getAmount()));

        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Long clientBalance = client.getAccount().getBalance();

        if(clientBalance >= money) {
            clientBalance -= money;
        } else {
            throw new BalancePositiveException("Ваш баланс не может быть отрицательным");
        }

        PhoneNumber phoneNumberSearch = null;
        Email emailSearch = null;

        if (searchClientForMoneyTransfer.getPhoneNumber() != null) {
            phoneNumberSearch = phoneNumberService.findByNumber(searchClientForMoneyTransfer.getPhoneNumber());
        }

        if (searchClientForMoneyTransfer.getEmail() != null) {
            emailSearch = emailService.findByEmail(searchClientForMoneyTransfer.getEmail());
        }

        Client clientSearch = clientRepository.findByParamsForMoneyTransfer(username, phoneNumberSearch, emailSearch);

        Long clientSearchBalance = clientSearch.getAccount().getBalance();

        clientSearchBalance += money;

        lock.lock();
        try {
            client.getAccount().setBalance(clientBalance);
            clientSearch.getAccount().setBalance(clientSearchBalance);

            clientRepository.save(client);
            clientRepository.save(clientSearch);
        } finally {
            lock.unlock();
        }

        return ChangeBalance.toDouble(clientBalance);
    }

    public Double getAccount() {
        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        return ChangeBalance.toDouble(client.getAccount().getBalance());
    }

    public Client findByUsername(String username) {
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с username = " + username + " не найден"));
    }

    public PhoneNumber updatePhoneNumber(PhoneNumberUpdate phoneNumberUpdate) {
        return phoneNumberService.update(phoneNumberUpdate.getOldPhoneNumber(),
                                phoneNumberUpdate.getNewPhoneNumber(),
                                getUserId());
    }

    public void deletePhoneNumber(String phoneNumber) {

        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        if (client.getPhoneNumbers().size() == 1) {
            throw new NumberOfPhoneNumbersException("У пользователя должен быть номер телефона.");
        }

        phoneNumberService.delete(phoneNumber, getUserId());
    }

    public PhoneNumber addPhoneNumber(String phoneNumber) {

        Optional<Client> client = clientRepository.findById(getUserId());
        PhoneNumber phoneNumberCreate = null;

        if (client.isPresent()) {
           phoneNumberCreate = phoneNumberService.create(phoneNumber, client.get());
        } else  {
            throw new CustomAccessDeniedException();
        }

        return phoneNumberCreate;
    }

    public Email updateEmail(EmailUpdate emailUpdate) {
        return emailService.update(emailUpdate.getOldEmail(),
                emailUpdate.getNewEmail(),
                getUserId());
    }

    public void deleteEmail(String email) {

        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        if (client.getPhoneNumbers().size() == 1) {
            throw new NumberOfPhoneNumbersException("У пользователя должен быть номер телефона.");
        }

        emailService.delete(email, getUserId());
    }

    public Email addEmail(String email) {

        Optional<Client> client = clientRepository.findById(getUserId());

        Email emailCreate = null;

        if (client.isPresent()) {
            emailCreate = emailService.create(email, client.get());
        } else  {
            throw new CustomAccessDeniedException();
        }

        return emailCreate;
    }

    /**
     * Получение ID пользователя. Из авторизации.
     * @return ID пользователя.
     */
    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtEntity client = (JwtEntity) authentication.getPrincipal();
        return client.getClient().getId();
    }

}
