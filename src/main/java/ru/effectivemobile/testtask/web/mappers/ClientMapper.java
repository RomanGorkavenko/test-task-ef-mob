package ru.effectivemobile.testtask.web.mappers;

import org.mapstruct.Mapper;
import ru.effectivemobile.testtask.model.Client;
import ru.effectivemobile.testtask.web.dto.ClientResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponse toDto(Client client);

    List<ClientResponse> toDto(List<Client> clients);
}
