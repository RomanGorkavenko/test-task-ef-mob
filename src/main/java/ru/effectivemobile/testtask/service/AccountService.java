package ru.effectivemobile.testtask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Account;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.repository.AccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account create(Double balance) {
        Account account = new Account();
        account.setNumber(UUID.randomUUID());
        account.setBalance(ChangeBalance.toLong(ChangeBalance.round(balance)));

        return accountRepository.save(account);
    }

    public void changeBalance(Client client) {
        Double balance = ChangeBalance.toDouble(client.getAccount().getBalance());
        Account account = client.getAccount();
        ChangeBalance.percentageIncrease(balance, account, accountRepository);
    }


}
