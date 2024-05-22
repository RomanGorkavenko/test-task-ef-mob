package ru.effectivemobile.testtask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.Email;
import ru.effectivemobile.testtask.repository.EmailRepository;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    public Email create(String email, Client client) {
        Email emailCreate = new Email();
        emailCreate.setEmail(email);
        emailCreate.setClient(client);
        return emailRepository.save(emailCreate);
    }
}
