package ru.effectivemobile.testtask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.repository.PhoneNumberRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneNumberService {

    private final PhoneNumberRepository phoneNumberRepository;

    public PhoneNumber findByNumber(String number) {
        return phoneNumberRepository.findByNumber(number)
                .orElseThrow(() -> new NoSuchElementException("Номер телефона не найден."));
    }

    public PhoneNumber create(String phoneNumber, Client client) {
        PhoneNumber phoneNumberCreate = new PhoneNumber();
        phoneNumberCreate.setNumber(phoneNumber);
        phoneNumberCreate.setClient(client);
        return phoneNumberRepository.save(phoneNumberCreate);
    }

    public PhoneNumber update(String oldPhoneNumber, String newPhoneNumber, Long clientId) {
        Optional<PhoneNumber> phoneNumberSearch = phoneNumberRepository
                .findByNumberAndClientId(oldPhoneNumber, clientId);

        if (phoneNumberSearch.isPresent()) {
            PhoneNumber phoneNumber = phoneNumberSearch.get();
            phoneNumber.setNumber(newPhoneNumber);
            return phoneNumberRepository.save(phoneNumber);
        } else {
            throw new NoSuchElementException("Такого номера телефона у вас нет.");
        }
    }

    public void delete(String phoneNumber, Long clientId) {
        PhoneNumber phoneNumberDelete = phoneNumberRepository
                .findByNumberAndClientId(phoneNumber, clientId)
                .orElseThrow(() -> new NoSuchElementException("Такого номера телефона у вас нет."));
        phoneNumberRepository.delete(phoneNumberDelete);
    }

}
