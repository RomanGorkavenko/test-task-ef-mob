package ru.effectivemobile.testtask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.repository.PhoneNumberRepository;

@Service
@RequiredArgsConstructor
public class PhoneNumberService {

    private final PhoneNumberRepository phoneNumberRepository;

    public PhoneNumber create(String phoneNumber, Client client) {
        PhoneNumber phoneNumberCreate = new PhoneNumber();
        phoneNumberCreate.setNumber(phoneNumber);
        phoneNumberCreate.setClient(client);
        return phoneNumberRepository.save(phoneNumberCreate);
    }

    public PhoneNumber get(Long id) {
        return phoneNumberRepository.findById(id).orElse(null);
    }
}
