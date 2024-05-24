package ru.effectivemobile.testtask.web.dto;

import lombok.Data;

@Data
public class JwtRequest {

    private String username;
    private String password;

}
