package ru.effectivemobile.testtask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.effectivemobile.testtask.config.TestConfig;
import ru.effectivemobile.testtask.exception.BalancePositiveException;
import ru.effectivemobile.testtask.model.Account;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.Email;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.repository.ClientRepository;
import ru.effectivemobile.testtask.search.SearchClientForMoneyTransfer;
import ru.effectivemobile.testtask.web.security.JwtEntity;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ClientServiceTest {

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PasswordEncoder testPasswordEncoder;

    @MockBean
    private PhoneNumberService phoneNumberService;

    @Autowired
    private ClientService clientService;

    private Account account;
    private Client client;
    private Account accountSearch;
    private Client clientSearch;
    private SearchClientForMoneyTransfer searchClientForMoneyTransfer;
    private Long clientId;
    private String username;

    @BeforeEach
    void setUp() {
        clientId = 1L;
        username = "username";
        String password = "password";

        account = new Account();

        client = new Client();
        client.setId(clientId);
        client.setAccount(account);
        client.setUsername(username);
        client.setPassword(testPasswordEncoder.encode(password));

        Long clientIdSearch = 2L;
        String usernameSearch = "usernameSearch";
        String passwordSearch = "passwordSearch";

        accountSearch = new Account();

        clientSearch = new Client();
        clientSearch.setId(clientIdSearch);
        clientSearch.setAccount(accountSearch);
        clientSearch.setUsername(usernameSearch);
        client.setPassword(testPasswordEncoder.encode(passwordSearch));

        searchClientForMoneyTransfer = new SearchClientForMoneyTransfer();

        JwtEntity jwtEntity = new JwtEntity(client);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(jwtEntity);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Тест проверяет получения клиента из БД.
     */
    @Test
    void moneyTransfer_ClientFindById() {
        account.setBalance(50000L);
        accountSearch.setBalance(500000L);

        searchClientForMoneyTransfer.setUsername("username");
        searchClientForMoneyTransfer.setAmount(200.00);

        assertNotNull(searchClientForMoneyTransfer.getUsername());

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        Mockito.when(clientRepository.findByParamsForMoneyTransfer(username, null, null))
                .thenReturn(clientSearch);

        Double balance = clientService.moneyTransfer(searchClientForMoneyTransfer);

        Mockito.verify(clientRepository, Mockito.times(1)).findById(clientId);
        Mockito.verify(clientRepository, Mockito.times(1)).save(client);
        Mockito.verify(clientRepository, Mockito.times(1)).save(clientSearch);
        Mockito.verify(clientRepository, Mockito.times(1))
                .findByParamsForMoneyTransfer(username, null, null);

        assertEquals(300.00, balance, 0.001);
    }

    /**
     * Тест проверяет что выбрасывается ошибка, если клиент не найден.
     */
    @Test
    void moneyTransfer_ClientFindByIdExistingId() {
        account.setBalance(50000L);

        searchClientForMoneyTransfer.setUsername("username");
        searchClientForMoneyTransfer.setAmount(200.00);

        assertNotNull(searchClientForMoneyTransfer.getUsername());

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> clientService.moneyTransfer(searchClientForMoneyTransfer));

        Mockito.verify(clientRepository, Mockito.times(1)).findById(clientId);
    }

    /**
     * Тест проверяет что выбрасывается ошибка, если баланс клиента может стать отрицательным.
     */
    @Test
    void moneyTransfer_BalancePositiveException() {
        account.setBalance(500L);

        searchClientForMoneyTransfer.setUsername("username");
        searchClientForMoneyTransfer.setAmount(200.00);

        assertNotNull(searchClientForMoneyTransfer.getUsername());

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(BalancePositiveException.class,
                () -> clientService.moneyTransfer(searchClientForMoneyTransfer));

        Mockito.verify(clientRepository, Mockito.times(1)).findById(clientId);
    }

    /**
     * Тест проверяет получение номера телефона от сервиса и получение клиента по нему из БД.
     */
    @Test
    void moneyTransfer_phoneNumberServiceFindByNumber() {
        account.setBalance(50000L);
        accountSearch.setBalance(500000L);

        searchClientForMoneyTransfer.setUsername(null);
        searchClientForMoneyTransfer.setPhoneNumber("phoneNumber");
        searchClientForMoneyTransfer.setAmount(200.00);

        assertNull(searchClientForMoneyTransfer.getUsername());

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("phoneNumber");

        Mockito.when(phoneNumberService.findByNumber(searchClientForMoneyTransfer
                .getPhoneNumber())).thenReturn(phoneNumber);

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        Mockito.when(clientRepository.findByParamsForMoneyTransfer(
                        searchClientForMoneyTransfer.getUsername(), phoneNumber, null))
                .thenReturn(clientSearch);

        Double balance = clientService.moneyTransfer(searchClientForMoneyTransfer);

        Mockito.verify(clientRepository, Mockito.times(1)).findById(clientId);
        Mockito.verify(clientRepository, Mockito.times(1)).save(client);
        Mockito.verify(clientRepository, Mockito.times(1)).save(clientSearch);
        Mockito.verify(clientRepository, Mockito.times(1))
                .findByParamsForMoneyTransfer(searchClientForMoneyTransfer.getUsername(), phoneNumber, null);

        assertEquals(300.00, balance, 0.001);
    }

    /**
     * Тест проверяет получение email от сервиса и получение клиента по нему из БД.
     */
    @Test
    void moneyTransfer_emailServiceFindByEmail() {
        account.setBalance(50000L);
        accountSearch.setBalance(500000L);

        searchClientForMoneyTransfer.setUsername(null);
        searchClientForMoneyTransfer.setEmail("email@email.com");
        searchClientForMoneyTransfer.setAmount(200.00);

        assertNull(searchClientForMoneyTransfer.getUsername());

        Email email = new Email();
        email.setEmail("email@email.com");

        Mockito.when(emailService.findByEmail(searchClientForMoneyTransfer
                .getEmail())).thenReturn(email);

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        Mockito.when(clientRepository.findByParamsForMoneyTransfer(
                        searchClientForMoneyTransfer.getUsername(), null, email))
                .thenReturn(clientSearch);

        Double balance = clientService.moneyTransfer(searchClientForMoneyTransfer);

        Mockito.verify(clientRepository, Mockito.times(1)).findById(clientId);
        Mockito.verify(clientRepository, Mockito.times(1)).save(client);
        Mockito.verify(clientRepository, Mockito.times(1)).save(clientSearch);
        Mockito.verify(clientRepository, Mockito.times(1))
                .findByParamsForMoneyTransfer(searchClientForMoneyTransfer.getUsername(), null, email);

        assertEquals(300.00, balance, 0.001);
    }
}