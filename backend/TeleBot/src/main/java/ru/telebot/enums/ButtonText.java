package ru.telebot.enums;

import java.util.Optional;

public enum ButtonText {
    ADD_CASHBACK("Накопить кэшбек"),
    SUB_CASHBACK("Списать баллы"),
    AVAILABILITY("Узнать наличие"),
    CASHBACK_RULES("Правила кэшбека"),
    CHANNEL_LINK("\uD83D\uDCE2 Подписаться"),
    SUBSCRIBED("✅ Я подписан"),
    USED_BUTTON("Б/У айфоны"),
    NEW_BUTTON("Новые айфоны"),
    BACK_BUTTON("↩️ Назад"),
    PHONE_BUTTON("\uD83D\uDCF1 iPhone ");

    private final String value;

    ButtonText(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Optional<ButtonText> fromValue(String value) {
        for (ButtonText command : ButtonText.values()) {
            if (value.equals(command.value)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }
}