package ru.telebot.bot.enums;

public enum ButtonText {
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
}