package ru.effectivemobile.testtask.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Фильтр для обработки JWT токена. До авторизации.
 * Для авторизации пользователя.
 */
@AllArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String bearerToken = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
        }
        if (bearerToken != null && jwtTokenProvider.validateToken(bearerToken)) {
            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(bearerToken);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (NoSuchElementException ignored) {
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
