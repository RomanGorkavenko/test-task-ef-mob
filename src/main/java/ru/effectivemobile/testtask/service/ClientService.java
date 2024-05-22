package ru.effectivemobile.testtask.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Account;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.repository.ClientRepository;
import ru.effectivemobile.testtask.web.dto.ClientRequest;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final AccountService accountService;
    private final EmailService emailService;
    private final PhoneNumberService phoneNumberService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackOn = Exception.class)
    public Client create(ClientRequest clientRequest) {

        Double balance = ChangeBalance.round(clientRequest.getBalance());

        Account account = accountService.create(balance);

        Client client = new Client();
        client.setAccount(account);
        client.setUsername(clientRequest.getUsername());
        client.setPassword(passwordEncoder.encode(clientRequest.getPassword()));
        Client clientResult = clientRepository.save(client);

        phoneNumberService.create(clientRequest.getPhoneNumber(), clientResult);
        emailService.create(clientRequest.getEmail(), clientResult);

        return clientResult;
    }

    public List<PhoneNumber> getPhoneNumbers(Long id) {

        return clientRepository.findById(id).get().getPhoneNumbers().stream().toList();
    }

    public Account getAccount(Long id) {

        return clientRepository.findById(id).get().getAccount();
    }

    public Client findByUsername(String username) {
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с username = " + username + " не найден"));
    }

}
