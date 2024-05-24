package ru.effectivemobile.testtask.web.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.testtask.exception.BalancePositiveException;
import ru.effectivemobile.testtask.exception.CustomAccessDeniedException;
import ru.effectivemobile.testtask.exception.NumberOfPhoneNumbersOrEmailException;
import ru.effectivemobile.testtask.web.dto.validation.ValidationErrorResponse;
import ru.effectivemobile.testtask.web.dto.validation.Violation;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Контроллер для обработки исключений.
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementException(NoSuchElementException e) {
        log.atLevel(Level.WARN).log(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> pSQLException(SQLException e) {
        log.atLevel(Level.WARN).log(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BalancePositiveException.class)
    public ResponseEntity<String> BalancePositiveException(BalancePositiveException e) {
        log.atLevel(Level.WARN).log(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.atLevel(Level.WARN).log(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<String> CustomAccessDeniedException(CustomAccessDeniedException e) {
        log.atLevel(Level.WARN).log(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NumberOfPhoneNumbersOrEmailException.class)
    public ResponseEntity<String> NumberOfPhoneNumbersException(NumberOfPhoneNumbersOrEmailException e) {
        log.atLevel(Level.WARN).log(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Обрабатывает все ConstraintViolationException,
     * которые пробрасываются до уровня контроллера.
     * @return список ошибок.
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e
    ) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    /**
     * Обрабатывает ошибки валидации для тел запросов
     * MethodArgumentNotValidExceptions
     * @return список ошибок.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

}
