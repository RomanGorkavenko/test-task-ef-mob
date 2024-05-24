package ru.effectivemobile.testtask.web.controller.impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.service.AccountService;
import ru.effectivemobile.testtask.service.AuthService;
import ru.effectivemobile.testtask.service.ClientService;
import ru.effectivemobile.testtask.web.controller.AuthController;
import ru.effectivemobile.testtask.web.dto.ClientRequest;
import ru.effectivemobile.testtask.web.dto.ClientResponse;
import ru.effectivemobile.testtask.web.dto.JwtRequest;
import ru.effectivemobile.testtask.web.dto.JwtResponse;
import ru.effectivemobile.testtask.web.mappers.ClientMapper;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Auth API")
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final ClientService clientService;
    private final ClientMapper clientMapper;
    private final AccountService accountService;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<ClientResponse> register(
            @RequestBody @Valid ClientRequest clientRequest) {

        Client client = clientService.create(clientRequest);

        // Запуск изменения баланса на 5% каждую минуту.
        accountService.changeBalance(client);

        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    @PostMapping("/refresh")
    @PreAuthorize("@customSecurityExpression.canAccessClient()")
    public JwtResponse refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }

}
