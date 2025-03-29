package ru.telebot.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.telebot.bot.TelegramBot;
import ru.telebot.bot.enums.CallbackData;
import ru.telebot.bot.enums.ScriptMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private TelegramBot bot;
    @Value("@${bot.channelName}")
    private String channelName;

    public void registerBot(TelegramBot bot) {
        this.bot = bot;
    }

    public void handleUpdate(Update update) {
        if (update == null) {
            log.error("переданный Update равен null");
        } else if(update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().isCommand()) {
                log.debug("Получена команда");
                handleCommand(update.getMessage());
            } else {
                log.debug("Получен текст");
                handleText(update);
            }
        } else if (update.hasCallbackQuery()) {
            log.debug("Получен callback");
            handleCallbackQuery(update.getCallbackQuery());
        } else if (update.getMessage().hasContact()) {
            log.debug("Получен контакт");
        }
    }

    private void handleText(Update update) {
        SendMessage message = new SendMessage();
        message.setText(update.getMessage().getText());
        message.setChatId(update.getMessage().getChatId());
        bot.sendMessage(message);
    }

    private void handleCommand(Message message) {
        switch (message.getText()) {
            case "/start": {
                if (!isUserSubscribed(message.getFrom().getId())) {
                    handleUnsubscribe(message);
                    return;
                }
                //telegramBot.forceSubscribeUser(update.getMessage().getChatId());
                //addUser(update.getMessage().getChatId());

                sendMessage(getStartMessage(message.getChatId()));

                break;
            }
            case "/help": {

            }
        }

    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String text = callbackQuery.getData();
        Optional<CallbackData> callbackData = CallbackData.fromValue(text);
        if (callbackData.isEmpty()) {
            log.error("Пустой CallbackData");
            return;
        }
        switch (callbackData.get()) {
            case CallbackData.START_SUBSCRIBED:
                EditMessageText editMessage = EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(callbackQuery.getMessage().getMessageId())
                        .text("")
                        .build();
                SendMessage newMessage = null;
                if (isUserSubscribed(callbackQuery.getFrom().getId())) {
                    editMessage.setText(String.valueOf(ScriptMessage.MESSAGE_AFTER_SUBSCRIBE));
                    newMessage = SendMessage.builder()
                            .chatId(chatId)
                            .replyMarkup(setUpKeyboardMarkup())
                            .text(String.valueOf(ScriptMessage.SUBSCRIBED_START))
                            .build();
                } else {
                    editMessage.setText(String.valueOf(ScriptMessage.REPEATED_UNSUBSCRIBED_MESSAGE));
                    editMessage.setReplyMarkup(getSubscribeMarkup());
                }
                bot.sendEditedMessage(editMessage);
                bot.sendMessage(newMessage);
                bot.sendCallbackAnswer(callbackQuery.getId());
                break;
        }
    }

    private SendMessage getStartMessage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(String.valueOf(ScriptMessage.SUBSCRIBED_START))
                .replyMarkup(setUpKeyboardMarkup())
                .build();
    }

    private void handleUnsubscribe(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(String.valueOf(ScriptMessage.UNSUBSCRIBED_START));
        sendMessage.setReplyMarkup(getSubscribeMarkup());

        sendMessage(sendMessage);
    }

    private InlineKeyboardMarkup getSubscribeMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton buttonLink = InlineKeyboardButton.builder()
                .url(getInviteLink())
                .text("Подписаться")
                .callbackData("subscribeLink")
                .build();
        row.add(buttonLink);
        row.add(getInlineKeyboardButton("Я подписан ✅", "subscribed"));

        markup.setKeyboard(List.of(row));
        return markup;
    }

    private String getInviteLink() {
        try {
            // Проверяем, есть ли у бота права администратора
            Chat chat = bot.execute(GetChat.builder().chatId(channelName).build());

            if (chat.isChannelChat()) {
                return chat.getInviteLink();
            } else {
                throw new TelegramApiException("Указанный ID не является каналом");
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка получения ссылки: {}", e.getMessage());
            return null;
        }
    }

    private boolean isUserSubscribed(Long userId) {
        try {
            ChatMember member = bot.execute(GetChatMember.builder()
                    .chatId(channelName)
                    .userId(userId)
                    .build());

            return member.getStatus().equals("member") ||
                    member.getStatus().equals("administrator") ||
                    member.getStatus().equals("creator");
        } catch (TelegramApiException e) {
            log.error("Ошибка проверки подписки на канал: {}", e.getMessage());
            return false;
        }
    }

    private ReplyKeyboardMarkup setUpKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .inputFieldPlaceholder("Test message")
                .build();

//        KeyboardButton keyboardButton = KeyboardButton.builder()
//                .requestContact(true)
//                .text("Поделиться контактом")
//                .build();

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(setKeyboardRow("Кэшбек", "Списать баллы"));
        rows.add(setKeyboardRow("Узнать наличие", "Правила кэшбека"));

        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }

    private KeyboardRow setKeyboardRow(String text1, String text2) {
        List<String> buttons = new ArrayList<>();
        buttons.add(text1);
        buttons.add(text2);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.addAll(buttons);
        return keyboardRow;
    }

    private static InlineKeyboardButton getInlineKeyboardButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    private void sendMessage(SendMessage message) {
        bot.sendMessage(message);
    }
}
