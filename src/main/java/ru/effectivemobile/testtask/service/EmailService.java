package ru.effectivemobile.testtask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.Email;
import ru.effectivemobile.testtask.repository.EmailRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    public Email findByEmail(String email) {
        return emailRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Адрес электронной почты не найден."));
    }

    public Email create(String email, Client client) {
        Email emailCreate = new Email();
        emailCreate.setEmail(email);
        emailCreate.setClient(client);
        return emailRepository.save(emailCreate);
    }

    public Email update(String oldEmail, String newEmail, Long clientId) {
        Optional<Email> emailSearch = emailRepository
                .findByEmailAndClientId(oldEmail, clientId);

        if (emailSearch.isPresent()) {
            Email email = emailSearch.get();
            email.setEmail(newEmail);
            return emailRepository.save(email);
        } else {
            throw new NoSuchElementException("Такого адреса электронной почты у вас нет.");
        }
    }

    public void delete(String email, Long clientId) {
        Email emailDelete = emailRepository
                .findByEmailAndClientId(email, clientId)
                .orElseThrow(() -> new NoSuchElementException("Такого адреса электронной почты у вас нет."));
        emailRepository.delete(emailDelete);
    }
}
