package ru.telebot.bot.enums;

import java.util.Optional;

public enum ScriptMessage {
    BOT_DESCRIPTION("С моей помощью ты сможешь узнать наличие товара в магазине и свой баланс бонусов"),
    BOT_SHORT_DESCRIPTION("Бот для системы лояльности и информации о наличии"),
    SUBSCRIBED_START("Добро пожаловать в бот!\nВыберите интересующий пункт в меню ниже"),
    UNSUBSCRIBED_START("Для того, чтобы начать копить бонусы, пожалуйста, подпишитесь на наш канал по кнопке ниже"),
    AFTER_SUBSCRIBE("Спасибо за подписку!"),
    REPEATED_UNSUBSCRIBED("Подписка не найдена. Попробуйте ещё раз"),
    UNKNOWN_COMMAND("Извините, я не понимаю эту команду\nДоступные команды представлены в меню слева от поля ввода сообщения");

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
