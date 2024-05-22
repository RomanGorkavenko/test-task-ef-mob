package ru.effectivemobile.testtask.web.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.testtask.web.dto.validation.ValidationErrorResponse;
import ru.effectivemobile.testtask.web.dto.validation.Violation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Контроллер для обработки исключений выбрасываемых
 * в других контроллерах.
 */
@Log
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementException(NoSuchElementException e) {
        log.log(Level.WARNING, e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
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
