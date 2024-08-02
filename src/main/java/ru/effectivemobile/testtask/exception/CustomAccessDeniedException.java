package ru.effectivemobile.testtask.exception;

/**
 * Это класс исключения, которое выбрасывается, если пользователь не имеет доступа к данному ресурсу.
 */
public class CustomAccessDeniedException extends RuntimeException {

    /**
     * Это исключение, которое выбрасывается, если пользователь не имеет доступа к данному ресурсу.
     */
    public CustomAccessDeniedException() {
        super("Unauthorized.");
    }
}
