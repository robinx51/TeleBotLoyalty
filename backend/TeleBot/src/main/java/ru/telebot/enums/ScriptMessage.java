package ru.telebot.enums;

public enum ScriptMessage {
    BOT_DESCRIPTION("С моей помощью ты сможешь узнать наличие товара в магазине и свой баланс бонусов"),
    BOT_SHORT_DESCRIPTION("Бот для системы лояльности и информации о наличии"),
    SUBSCRIBED_START("Добро пожаловать в бот!\nВыберите интересующий пункт в меню ниже"),
    UNSUBSCRIBED_START("Для того, чтобы начать копить бонусы, пожалуйста, подпишитесь на наш канал по кнопке ниже"),
    AFTER_SUBSCRIBE("Спасибо за подписку!"),
    REPEATED_UNSUBSCRIBED("Подписка не найдена. Попробуйте ещё раз"),
    UNKNOWN_COMMAND("Извините, я не понимаю эту команду\nДоступные команды представлены в меню слева и справа от поля ввода сообщения"),
    ADD_CASHBACK_RESPONSE("Чтобы накопить кэшбэк, назовите этот код мененджеру: "),
    SUB_CASHBACK_RESPONSE("Чтобы списать баллы, назовите этот код мененджеру: "),
    PHONES_TEXT("Выберите тип товара:ㅤㅤㅤㅤ"),
    MODEL_TEXT("Выберите поколение:ㅤㅤㅤㅤㅤ"),
    TYPE_TEXT("\nВыберите тип смартфона:ㅤㅤㅤㅤㅤ"),
    MEMORY_TEXT("Укажите количество памяти:"),
    CHOICE_TEXT("iPhone "),
    RESPONSE_TEXT("Общий баланс: "); // Сообщение после операции
    private final String value;

    ScriptMessage(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
