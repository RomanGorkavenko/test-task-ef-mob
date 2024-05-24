package ru.effectivemobile.testtask.web.dto;

import lombok.Data;

/**
 * Ответ при возвращении email
 */
@Data
public class EmailResponse {

    private String email;
    private String description;
}
