package ru.effectivemobile.testtask.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Достает из конфигурационного файла конфигурацию JWT
 */
@Component
@Data
@ConfigurationProperties("security.jwt")
public class JwtProperties {

    /**
     * Секретный ключ.
     */
    private String secret;

    /**
     * Жизненный цикл access token.
     */
    private Long access;

    /**
     * Жизненный цикл refresh token.
     */
    private Long refresh;

}
