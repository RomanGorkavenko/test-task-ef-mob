package ru.effectivemobile.testtask.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Запрос при обновлении номера телефона.
 */
@Data
public class PhoneNumberUpdate {

    @NotNull
    private String oldPhoneNumber;

    @NotNull
    private String newPhoneNumber;

}
