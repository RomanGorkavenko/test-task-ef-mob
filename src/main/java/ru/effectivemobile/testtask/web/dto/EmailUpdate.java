package ru.effectivemobile.testtask.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmailUpdate {

    @NotNull
    private String oldEmail;

    @NotNull
    private String newEmail;
}
