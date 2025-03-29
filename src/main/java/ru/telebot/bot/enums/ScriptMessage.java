package ru.telebot.bot.enums;

import java.util.Optional;

public enum ScriptMessage {
    SUBSCRIBED_START("Добро пожаловать в бот!\nВыберите интересующий пункт в меню ниже"),
    UNSUBSCRIBED_START("Для продолжения работы с ботом, пожалуйста, подпишитесь на наш канал по кнопке ниже"),
    MESSAGE_AFTER_SUBSCRIBE("Спасибо за подписку!"),
    REPEATED_UNSUBSCRIBED_MESSAGE("Подписка не найдена. Попробуйте ещё раз");

    private final String value;

    ScriptMessage(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Optional<ScriptMessage> fromValue(String v) {
        for (ScriptMessage command : ScriptMessage.values()) {
            if (v.equals(command.value)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }
}
