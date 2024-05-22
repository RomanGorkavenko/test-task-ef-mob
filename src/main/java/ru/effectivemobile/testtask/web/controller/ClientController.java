package ru.effectivemobile.testtask.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.service.AccountService;
import ru.effectivemobile.testtask.service.ChangeBalance;
import ru.effectivemobile.testtask.service.ClientService;
import ru.effectivemobile.testtask.web.dto.ClientRequest;
import ru.effectivemobile.testtask.web.dto.ClientResponse;
import ru.effectivemobile.testtask.web.mappers.ClientMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/client")
public class ClientController {

    private final ClientService clientService;
    private final AccountService accountService;
    private final ClientMapper clientMapper;

    @PostMapping("/create")
    public ResponseEntity<ClientResponse> create(
            @RequestBody @Valid ClientRequest clientRequest) {

        Client client = clientService.create(clientRequest);

        // Запуск изменения баланса на 5% каждую минуту.
        accountService.changeBalance(client);

        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    @GetMapping("/get")
    public ResponseEntity<List<PhoneNumber>> get(@RequestParam Long id) {
        return ResponseEntity.ok(clientService.getPhoneNumbers(id));
    }

    @GetMapping("/get/balance")
    public ResponseEntity<Double> getBalance(@RequestParam Long id) {

        return ResponseEntity.ok(ChangeBalance.toDouble(clientService.getAccount(id).getBalance()));
    }
}
