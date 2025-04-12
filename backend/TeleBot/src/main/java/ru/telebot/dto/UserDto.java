package ru.telebot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    @NotNull @Schema(name = "code", example = "99999")
    private Integer code;
    @NotNull @Schema(name = "telegramId", example = "1234567890")
    private Long telegramId;
    @Schema(name = "username", example = "ivanov")
    private String username;
    @Schema(name = "first&lastName", example = "Александр Иванов")
    private String name;
    @Schema(name = "phoneNumber", example = "+7 (987) 654-32-10")
    private String phoneNumber;
    @Schema(name = "cashback", example = "1000")
    private int cashback;
}
