package ru.effectivemobile.testtask.exception;

/**
 * Выбрасывается при попытке удалить последний номер телефона или email у клиента.
 */
public class NumberOfPhoneNumbersOrEmailException extends RuntimeException {

    public NumberOfPhoneNumbersOrEmailException(String message) {
        super(message);
    }
}
