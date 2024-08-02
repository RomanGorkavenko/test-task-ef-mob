package ru.effectivemobile.testtask.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import ru.effectivemobile.testtask.web.dto.validation.ValidPassword;

/**
 * Запрос при создании клиента.
 */
@Data
public class ClientRequest {

    @NotBlank(message = "Логин не должен быть пустой")
    private String username;

    @NotBlank(message = "Пароль не должен быть пустым")
    @ValidPassword
    private String password;

    @PositiveOrZero(message = "Баланс не должен быть отрицательным")
    private Double balance;

    @NotBlank(message = "Номер телефона не должен быть пустой")
    private String phoneNumber;

    @Email(message = "Адрес электронной почты должен быть действительным")
    @NotBlank(message = "Адрес электронной почты не должен быть пустой")
    private String email;
}
