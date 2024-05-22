package ru.effectivemobile.testtask.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.web.dto.JwtRequest;
import ru.effectivemobile.testtask.web.dto.JwtResponse;
import ru.effectivemobile.testtask.web.security.JwtTokenProvider;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final ClientService clientService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Проверка авторизации пользователя.
     * @param loginRequest - запрос на авторизацию
     * @return {@link JwtResponse} dto - ответ на запрос
     */
    public JwtResponse login(JwtRequest loginRequest) {
        JwtResponse jwtResponse = new JwtResponse();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        Client client = clientService.findByUsername(loginRequest.getUsername());
        jwtResponse.setId(client.getId());
        jwtResponse.setUsername(client.getUsername());
        jwtResponse.setAccessToken(jwtTokenProvider.createAccessToken(client.getId(),
                client.getUsername()));
        jwtResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(client.getId(), client.getUsername()));

        return jwtResponse;
    }

    /**
     * Обновление токена.
     * @param refreshToken - токен для обновления.
     * @return {@link JwtResponse} dto - ответ на запрос
     */
    public JwtResponse refresh(String refreshToken) {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }

}
