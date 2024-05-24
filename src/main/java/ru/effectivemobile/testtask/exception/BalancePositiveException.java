package ru.effectivemobile.testtask.exception;

/**
 * Выбрасывается если баланс может стать отрицательным.
 */
public class BalancePositiveException extends RuntimeException {

    public BalancePositiveException(String message) {
        super(message);
    }
}
