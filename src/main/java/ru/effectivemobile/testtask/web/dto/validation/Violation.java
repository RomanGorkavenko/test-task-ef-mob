package ru.effectivemobile.testtask.web.dto.validation;

/**
 * Класс сообщения об ошибке.
 * @param fieldName имя поля с ошибкой.
 * @param message сообщение о том какая ошибка произошла.
 */
public record Violation(String fieldName, String message) {

}
