package ru.telebot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    @Schema(name = "telegramId", example = "1234567890")
    private String telegramId;

    @Schema(name = "firstName", example = "Александр")
    private String firstName;

    @Schema(name = "lastName", example = "Иванов")
    private String lastName;

    @Schema(name = "username", example = "ivanov")
    private String username;

    @Schema(name = "status", example = "add/sub")
    private String status;

    @Schema(name = "purchaseAmount", example = "69990.00")
    private Double purchaseAmount;
}
