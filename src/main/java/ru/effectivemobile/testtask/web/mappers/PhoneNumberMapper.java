package ru.effectivemobile.testtask.web.mappers;

import org.mapstruct.Mapper;
import ru.effectivemobile.testtask.model.PhoneNumber;
import ru.effectivemobile.testtask.web.dto.PhoneNumberResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PhoneNumberMapper {
    PhoneNumberResponse toDto(PhoneNumber phoneNumber);

    List<PhoneNumberResponse> toDto(List<PhoneNumber> phoneNumbers);
}
