package ru.effectivemobile.testtask.web.dto;

import lombok.Data;

import java.util.Date;

/**
 * Ответ при возвращении клиента.
 */
@Data
public class ClientResponse {

    private Long id;
    private String username;
    private String fullName;
    private Date birthdate;

}
