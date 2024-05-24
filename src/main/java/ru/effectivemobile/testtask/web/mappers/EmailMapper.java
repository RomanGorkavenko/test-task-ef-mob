package ru.effectivemobile.testtask.web.mappers;

import org.mapstruct.Mapper;
import ru.effectivemobile.testtask.model.Email;
import ru.effectivemobile.testtask.web.dto.EmailResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    EmailResponse toDto(Email email);

    List<EmailResponse> toDto(List<Email> emails);
}
