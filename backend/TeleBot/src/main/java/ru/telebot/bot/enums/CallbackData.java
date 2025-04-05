package ru.telebot.bot.enums;

import java.util.Optional;

public enum CallbackData {
    START_SUBSCRIBED("subscribed"),
    PHONES_PAGE("phones"),
    USED_PAGE("used"),
    NEW_PAGE("new"),
    PAGE("page_"),
    ACTION("action_"),
    COMMON_MODEL("common"),
    PRO_TYPE("Pro"),
    PRO_MAX_TYPE("Pro Max"),
    MINI_TYPE("mini"),
    PLUS_TYPE("Plus"),
    E_TYPE("e");
//page_phones_used_15_pro_256
    private final String value;

    CallbackData(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Optional<CallbackData> fromValue(String value) {
        for (CallbackData command : CallbackData.values()) {
            if (value.equals(command.value)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }
}