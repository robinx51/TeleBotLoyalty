package ru.telebot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder
@AllArgsConstructor
public class UpdateUserDto {
    private Integer code;
    private Long telegramId;
    private String name;
    private String phoneNumber;
    private float cashback;
    private String action;
    private Long purchaseAmount;
    private int operationAmount;
}
