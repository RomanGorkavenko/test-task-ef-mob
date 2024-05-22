package ru.effectivemobile.testtask.web.dto;

import lombok.Data;
import ru.effectivemobile.testtask.model.Account;


import java.time.LocalDate;

@Data
public class ClientResponse {

    private Long id;
    private Account account;
    private String username;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate birthdate;

}
