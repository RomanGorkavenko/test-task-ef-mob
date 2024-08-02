package ru.effectivemobile.testtask.web.dto.validation;

import java.util.List;

/**
 * Класс для определения структуры ответа об ошибках пользователю.
 *
 * @param violations список сообщений об ошибках.
 */
public record ValidationErrorResponse(List<Violation> violations) {

}
