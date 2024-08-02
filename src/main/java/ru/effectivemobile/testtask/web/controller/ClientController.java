package ru.effectivemobile.testtask.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import ru.effectivemobile.testtask.search.ClientSearchValues;
import ru.effectivemobile.testtask.search.SearchClientForMoneyTransfer;
import ru.effectivemobile.testtask.web.dto.*;

@Tag(name = "Client Controller", description = "Client API")
public interface ClientController {

    @Operation(summary = "Проверить баланс.", description = "Возвращает баланс аутентифицированного пользователя")
    ResponseEntity<Double> getBalance();

    @Operation(summary = "Поиск по параметрам.", description = "Возвращает Клиента.")
    ResponseEntity<Page<ClientResponse>> findByParams(ClientSearchValues clientSearchValues);

    @Operation(summary = "Перевод денег.", description = "Со счета аутентифицированного клиента," +
            "на счёт другого клиента.")
    ResponseEntity<Double> moneyTransfer(SearchClientForMoneyTransfer moneyTransfer);

    @Operation(summary = "Обновить номер телефона.", description = "Обновляет номер телефона.")
    ResponseEntity<PhoneNumberResponse> updatePhoneNumber(PhoneNumberUpdate phoneNumberUpdate);

    @Operation(summary = "Удалить номер телефона.", description = "Удаляет номер телефона.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Номер телефона удален", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(example = "Номер телефона удален"))
            }),
            @ApiResponse(responseCode = "404", description = "Не найден пользователь.", content = {
                    @Content(mediaType = "text/plain",
                            schema = @Schema(example = "Пользователь не найден"))
            }),
            @ApiResponse(responseCode = "409", description = "Попытка удалить последний номер.", content = {
                    @Content(mediaType = "text/plain",
                            schema = @Schema(example = "У пользователя должен быть номер телефона."))
            })
    })
    ResponseEntity<String> deletePhoneNumber(@Parameter(name = "phoneNumber", description = "Номер телефона клиента",
            examples =
                    {@ExampleObject(name = "Рабочий", value = "+7(999) 022 32 20",
                            description = "Удалить номер \"+7(999) 022 32 20\""),
                            @ExampleObject(name = "Домашний", value = "246 56 00",
                                    description = "Удалить номер \"246 56 00\"")})
                                             String phoneNumber);

    @Operation(summary = "Добавить номер телефона.", description = "Добавляет номер телефона.")
    ResponseEntity<PhoneNumberResponse> addPhoneNumber(String phoneNumber);

    @Operation(summary = "Обновить email.", description = "Обновляет email.")
    ResponseEntity<EmailResponse> updateEmail(EmailUpdate emailUpdate);

    @Operation(summary = "Удалить email.", description = "Удаляет email.")
    ResponseEntity<String> deleteEmail(String email);

    @Operation(summary = "Добавить email.", description = "Добавляет email.")
    ResponseEntity<EmailResponse> addEmail(String email);
}
