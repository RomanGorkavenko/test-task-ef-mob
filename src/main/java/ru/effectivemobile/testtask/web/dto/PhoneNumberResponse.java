package ru.effectivemobile.testtask.web.dto;

import lombok.Data;

/**
 * Ответ при возвращении номера телефона.
 */
@Data
public class PhoneNumberResponse {

    private String number;
    private String description;

}
