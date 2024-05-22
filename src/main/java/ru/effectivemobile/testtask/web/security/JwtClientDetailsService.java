package ru.effectivemobile.testtask.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.service.ClientService;

/**
 * Сервис для авторизации пользователя.
 */
@Service
@RequiredArgsConstructor
public class JwtClientDetailsService implements UserDetailsService {

    private final ClientService clientService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientService.findByUsername(username);
        return new JwtEntity(client);
    }

}