package ru.effectivemobile.testtask.search;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 * Класс данных для перевода денег.
 */
@Data
public class SearchClientForMoneyTransfer {

    @PositiveOrZero(message = "Сумма не должен быть отрицательным")
    private Double amount;

    @Email(message = "Адрес электронной почты должен быть действительным")
    private String email;
    private String username;
    private String phoneNumber;

}
