package ru.effectivemobile.testtask.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Запрос при обновлении email.
 */
@Data
public class EmailUpdate {

    @NotNull
    private String oldEmail;

    @NotNull
    private String newEmail;
}
