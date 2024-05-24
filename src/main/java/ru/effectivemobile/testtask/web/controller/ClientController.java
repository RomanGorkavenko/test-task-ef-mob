package ru.effectivemobile.testtask.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.testtask.aspect.Loggable;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.search.ClientSearchValues;
import ru.effectivemobile.testtask.search.SearchClientForMoneyTransfer;
import ru.effectivemobile.testtask.service.ClientService;
import ru.effectivemobile.testtask.web.dto.*;
import ru.effectivemobile.testtask.web.mappers.ClientMapper;
import ru.effectivemobile.testtask.web.mappers.EmailMapper;
import ru.effectivemobile.testtask.web.mappers.PhoneNumberMapper;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/client")
@Tag(name = "Client Controller", description = "Client API")
public class ClientController {

    private final ClientService clientService;

    private final ClientMapper clientMapper;
    private final PhoneNumberMapper phoneNumberMapper;
    private final EmailMapper emailMapper;

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance() {
        return ResponseEntity.ok(clientService.getAccount());
    }

    @Loggable(level = Level.WARN)
    @PostMapping("/find-by-prams")
    public ResponseEntity<Page<ClientResponse>> findByParams(@RequestBody
                                                            @Valid ClientSearchValues clientSearchValues) {
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

        Page<Client> clientPage = clientService.findByParams(birthdate, fullName, phoneNumber, email, pageRequest);
        Page<ClientResponse> dto = clientPage.map(clientMapper::toDto);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/money-transfer")
    public ResponseEntity<Double> moneyTransfer(@RequestBody @Valid SearchClientForMoneyTransfer moneyTransfer) {
        return ResponseEntity.ok(clientService.moneyTransfer(moneyTransfer));
    }

    @PutMapping("/update/phone-number")
    public ResponseEntity<PhoneNumberResponse> updatePhoneNumber(@RequestBody
                                                                     @Valid PhoneNumberUpdate phoneNumberUpdate) {
        return ResponseEntity.ok(phoneNumberMapper.toDto(clientService.updatePhoneNumber(phoneNumberUpdate)));
    }

    @DeleteMapping("/delete/phone-number")
    public ResponseEntity<String> deletePhoneNumber(@RequestParam(name = "phoneNumber") String phoneNumber) {
        clientService.deletePhoneNumber(phoneNumber);
        return new ResponseEntity<>("Номер телефона удален", HttpStatus.OK);
    }

    @PostMapping("/add/phone-number")
    public ResponseEntity<PhoneNumberResponse> addPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        return ResponseEntity.ok(phoneNumberMapper.toDto(clientService.addPhoneNumber(phoneNumber)));
    }

    @PutMapping("/update/email")
    public ResponseEntity<EmailResponse> updateEmail(@RequestBody
                                                                 @Valid EmailUpdate emailUpdate) {
        return ResponseEntity.ok(emailMapper.toDto(clientService.updateEmail(emailUpdate)));
    }

    @DeleteMapping("/delete/email")
    public ResponseEntity<String> deleteEmail(@RequestParam(name = "email") String email) {
        clientService.deleteEmail(email);
        return new ResponseEntity<>("Номер телефона удален", HttpStatus.OK);
    }

    @PostMapping("/add/email")
    public ResponseEntity<EmailResponse> addEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(emailMapper.toDto(clientService.addEmail(email)));
    }
}
