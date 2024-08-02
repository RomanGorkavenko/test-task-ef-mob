package ru.effectivemobile.testtask.exception;

/**
 * Класс исключения, которое выбрасывается при попытке создания элемента, если такой элемент уже существует.
 */
public class ElementAlreadyExistsException extends RuntimeException {

    /**
     * Это исключение, которое выбрасывается при попытке создания элемента, если такой элемент уже существует.
     *
     * @param message сообщение об ошибке.
     */
    public ElementAlreadyExistsException(String message) {
        super(message);
    }
}
