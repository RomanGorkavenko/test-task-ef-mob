package ru.effectivemobile.testtask.search;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Возможные значения, по которым можно искать клиентов + значения сортировки
 */
@Data
public class ClientSearchValues {

    // поля поиска (все типы - объектные, не примитивные. Чтобы можно было передать null)
    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthdate;
    private String fullName;
    private String phoneNumber;

    @Email(message = "Адрес электронной почты должен быть действительным")
    private String email;

    // постраничность
    @NotNull
    private Integer pageNumber;
    @NotNull
    private Integer pageSize;

    // сортировка
    @NotBlank
    private String sortColumn;
    @NotBlank
    private String sortDirection;

}
