package ru.telebot.bot.enums;

import java.util.Optional;

public enum CallbackData {
    START_SUBSCRIBED("subscribed");

    private final String value;

    CallbackData(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Optional<CallbackData> fromValue(String v) {
        for (CallbackData command : CallbackData.values()) {
            if (v.equals(command.value)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }
}