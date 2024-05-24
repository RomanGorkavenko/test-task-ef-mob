package ru.effectivemobile.testtask.web.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.effectivemobile.testtask.exception.CustomAccessDeniedException;
import ru.effectivemobile.testtask.web.security.JwtEntity;


/**
 * Сервис для проверки авторизованности пользователя, и его прав доступа.
 */
@Service("customSecurityExpression")
@RequiredArgsConstructor
public class CustomSecurityExpression {

    /**
     * Проверка авторизации пользователя.
     * Проверяет, совпадает ли имя пользователя с именем авторизованного пользователя.
     * @param username - имя пользователя.
     * @return true - если имя пользователя совпадает с именем авторизованного пользователя иначе false.
     * @throws CustomAccessDeniedException - если пользователь не авторизован.
     */
    public boolean canAccessClient(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new CustomAccessDeniedException();
        }

        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        String currentUsername = user.getUsername();

        return currentUsername.equals(username);

    }

    /**
     * Проверка авторизации пользователя.
     */
    public boolean canAccessClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new CustomAccessDeniedException();
        }

        return authentication.isAuthenticated();

    }

}
