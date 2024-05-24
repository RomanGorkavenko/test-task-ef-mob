package ru.effectivemobile.testtask.config;

import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effectivemobile.testtask.repository.ClientRepository;
import ru.effectivemobile.testtask.service.*;
import ru.effectivemobile.testtask.service.props.JwtProperties;
import ru.effectivemobile.testtask.web.security.JwtClientDetailsService;
import ru.effectivemobile.testtask.web.security.JwtTokenProvider;

@TestConfiguration
@RequiredArgsConstructor
public class TestConfig {

    private final ClientRepository clientRepository;
    private final AuthenticationManager authenticationManager;

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtProperties jwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("d2VyeXdlcmtqbDtubDttcGloamd2YnV3ZWI3YWV3cg==");
        return jwtProperties;
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new JwtClientDetailsService(clientService());
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtProperties(), userDetailsService(), clientService());
    }

    @Bean
    @Primary
    public ClientService clientService() {
        return new ClientService(clientRepository, accountService(),
                emailService(), phoneNumberService(), testPasswordEncoder());
    }

    @Bean
    public AccountService accountService() {
        return Mockito.mock(AccountService.class);
    }

    @Bean
    public EmailService emailService() {
        return Mockito.mock(EmailService.class);
    }

    @Bean
    public PhoneNumberService phoneNumberService() {
        return Mockito.mock(PhoneNumberService.class);
    }

    @Bean
    @Primary
    public AuthService authService() {
        return new AuthService(authenticationManager, clientService(), jwtTokenProvider());
    }

}
