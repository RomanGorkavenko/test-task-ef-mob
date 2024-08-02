package ru.effectivemobile.testtask.service;

import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.aspect.Loggable;
import ru.effectivemobile.testtask.exception.BalancePositiveException;
import ru.effectivemobile.testtask.exception.CustomAccessDeniedException;
import ru.effectivemobile.testtask.exception.NumberOfPhoneNumbersOrEmailException;
import ru.effectivemobile.testtask.model.Account;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.Email;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.repository.ClientRepository;
import ru.effectivemobile.testtask.search.ClientSearchValues;
import ru.effectivemobile.testtask.search.SearchClientForMoneyTransfer;
import ru.effectivemobile.testtask.web.dto.ClientRequest;
import ru.effectivemobile.testtask.web.dto.EmailUpdate;
import ru.effectivemobile.testtask.web.dto.PhoneNumberUpdate;
import ru.effectivemobile.testtask.web.security.JwtEntity;

import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    /**
     * Создание клиента.
     * Использую библиотеку JavaFaker для генерации данных, так как при создании клиента пользователь их не вводит,
     * но они должны быть заполнены.
     *
     * @param clientRequest запрос для создания клиента по условиям в ТЗ.
     * @return клиента.
     */
    @Loggable(level = Level.WARN)
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

    /**
     * Поиск по параметрам с пагинацией и сортировкой.
     *
     * @return результат по странично.
     */
    @Loggable(level = Level.INFO)
    public Page<Client> findByParams(ClientSearchValues clientSearchValues) {

        // исключить NullPointerException
        Date birthdate = clientSearchValues.getBirthdate() != null ? clientSearchValues.getBirthdate() : null;
        String fullName = clientSearchValues.getFullName() != null ? clientSearchValues.getFullName() : null;
        String phoneNumber = clientSearchValues.getPhoneNumber() != null ? clientSearchValues.getPhoneNumber() : null;
        String email = clientSearchValues.getEmail() != null ? clientSearchValues.getEmail() : null;

        String sortColumn = clientSearchValues.getSortColumn() != null ? clientSearchValues.getSortColumn() : null;
        String sortDirection = clientSearchValues.getSortDirection() != null ? clientSearchValues.getSortDirection() : null;

        Integer pageNumber = clientSearchValues.getPageNumber();
        Integer pageSize = clientSearchValues.getPageSize();

        Sort.Direction direction =
                sortDirection == null || sortDirection.trim().isEmpty() || sortDirection.trim().equals("ask")
                        ? Sort.Direction.ASC : Sort.Direction.DESC;

        // объект сортировки, который содержит столбец и направление
        Sort sort = Sort.by(direction, sortColumn);

        // объект постраничности
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        PhoneNumber phoneNumberSearch = null;
        Email emailSearch = null;

        if (phoneNumber != null) {
            phoneNumberSearch = phoneNumberService.findByNumber(phoneNumber);
        }

        if (email != null) {
            emailSearch = emailService.findByEmail(email);
        }

        return clientRepository.findByParams(birthdate, fullName, phoneNumberSearch, emailSearch, pageRequest);

    }

    /**
     * Функционал перевода денег с одного счета на другой.
     * Со счета аутентифицированного пользователя, насчёт другого пользователя.
     * Добавлена проверка на отрицательный баланс.
     * Работа с базой потока безопасна.
     *
     * @param searchClientForMoneyTransfer параметры поиска клиента для перевода.
     * @return остаток на балансе клиента после перевода денег.
     */
    @Loggable(level = Level.WARN)
    @Transactional
    public Double moneyTransfer(SearchClientForMoneyTransfer searchClientForMoneyTransfer) {
        String username = searchClientForMoneyTransfer.getUsername() != null
                ? searchClientForMoneyTransfer.getUsername() : null;

        Long amount = ChangeBalance
                .toLong(ChangeBalance.round(searchClientForMoneyTransfer.getAmount()));

        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Long clientBalance = client.getAccount().getBalance();

        if (clientBalance >= amount) {
            clientBalance -= amount;
        } else {
            throw new BalancePositiveException("Ваш баланс не может быть отрицательным");
        }

        PhoneNumber phoneNumberSearch = null;
        Email emailSearch = null;

        if (searchClientForMoneyTransfer.getPhoneNumber() != null) {
            phoneNumberSearch = phoneNumberService
                    .findByNumber(searchClientForMoneyTransfer.getPhoneNumber());
        }

        if (searchClientForMoneyTransfer.getEmail() != null) {
            emailSearch = emailService.findByEmail(searchClientForMoneyTransfer.getEmail());
        }

        Client clientSearch = clientRepository
                .findByParamsForMoneyTransfer(username, phoneNumberSearch, emailSearch);

        Long clientSearchBalance = clientSearch.getAccount().getBalance();

        clientSearchBalance += amount;

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

    @Loggable(level = Level.INFO)
    public Double getBalance() {
        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        return ChangeBalance.toDouble(client.getAccount().getBalance());
    }

    @Loggable(level = Level.INFO)
    public Client findByUsername(String username) {
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь с username = " + username + " не найден"
                ));
    }

    @Loggable(level = Level.INFO)
    public PhoneNumber updatePhoneNumber(PhoneNumberUpdate phoneNumberUpdate) {
        return phoneNumberService.update(phoneNumberUpdate.getOldPhoneNumber(),
                phoneNumberUpdate.getNewPhoneNumber(),
                getUserId());
    }

    @Loggable(level = Level.INFO)
    public void deletePhoneNumber(String phoneNumber) {
        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        if (client.getPhoneNumbers().size() == 1) {
            throw new NumberOfPhoneNumbersOrEmailException("У пользователя должен быть номер телефона.");
        }

        phoneNumberService.delete(phoneNumber, getUserId());
    }

    @Loggable(level = Level.INFO)
    public PhoneNumber addPhoneNumber(String phoneNumber) {
        Optional<Client> client = clientRepository.findById(getUserId());

        PhoneNumber phoneNumberCreate;

        if (client.isPresent()) {
            phoneNumberCreate = phoneNumberService.create(phoneNumber, client.get());
        } else {
            throw new CustomAccessDeniedException();
        }

        return phoneNumberCreate;
    }

    @Loggable(level = Level.INFO)
    public Email updateEmail(EmailUpdate emailUpdate) {
        return emailService.update(emailUpdate.getOldEmail(),
                emailUpdate.getNewEmail(),
                getUserId());
    }

    @Loggable(level = Level.INFO)
    public void deleteEmail(String email) {
        Client client = clientRepository.findById(getUserId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        if (client.getPhoneNumbers().size() == 1) {
            throw new NumberOfPhoneNumbersOrEmailException(
                    "У пользователя должен быть адрес электронной почты."
            );
        }

        emailService.delete(email, getUserId());
    }

    @Loggable(level = Level.INFO)
    public Email addEmail(String email) {
        Optional<Client> client = clientRepository.findById(getUserId());

        Email emailCreate;

        if (client.isPresent()) {
            emailCreate = emailService.create(email, client.get());
        } else {
            throw new CustomAccessDeniedException();
        }

        return emailCreate;
    }

    /**
     * Получение ID пользователя. Из авторизации.
     *
     * @return ID пользователя.
     */
    @Loggable(level = Level.WARN)
    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtEntity client = (JwtEntity) authentication.getPrincipal();
        return client.getClient().getId();
    }

}
