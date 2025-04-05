package ru.telebot.bot.service;

import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import ru.telebot.dto.PhoneDto;

import java.util.*;
import java.util.stream.Collectors;

import static ru.telebot.bot.enums.ButtonText.*;
import static ru.telebot.bot.enums.CallbackData.*;
import static ru.telebot.bot.enums.ScriptMessage.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private TelegramBot bot;
    private final DataStorageService dataStorageService;
    @Value("@${bot.channelName}")
    private String channelName;
    private List<PhoneDto> phones;

    public void registerBot(TelegramBot bot) {
        this.bot = bot;
        initPhones();
    }

    // Handlers
    public void handleUpdate(Update update) {
        if (update == null) {
            log.error("переданный Update равен null");
        }
        else if(update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().isCommand()) {
                log.debug("Получена команда");
                handleCommand(update.getMessage());
            }
            else {
                log.debug("Получен текст");
                handleText(update);
            }
        }
        else if (update.hasCallbackQuery()) {
            log.debug("Получен callback: {}", update.getCallbackQuery().getData());
            handleCallbackQuery(update.getCallbackQuery());
        }
        else
            log.warn("Необработанный запрос: {}", update);
    }
    private void handleText(Update update) {
        String text = update.getMessage().getText();
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(" ")
                .build();
        switch (text) {
            case "Кэшбек" -> message.setText("Здесь будет начисляться кэшбэк после покупки");
            case "Списать баллы" -> message.setText("Здесь будет запрос на списание баллов");
            case "Узнать наличие" -> {
                EditMessageText availabilityPage = getAvailabilityPage();
                message.setText(availabilityPage.getText());
                message.setReplyMarkup(availabilityPage.getReplyMarkup());
            }
            case "Правила кэшбека" -> message.setText("Здесь будут правила кэшбека");
            default -> message.setText("Неизвестная команда, попробуйте ещё раз");
        }
        bot.sendMessage(message);
    }
    private void handleCommand(Message message) {
        switch (message.getText()) {
            case "/start": {
                if (!isUserSubscribed(message.getFrom().getId())) {
                    handleUnsubscribe(message);
                    return;
                }
                bot.sendMessage(getStartPage(message.getChatId()));
                break;
            } case "/help": {

            } default: {
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(message.getChatId())
                        .text(String.valueOf(UNKNOWN_COMMAND))
                        .build();
                bot.sendMessage(sendMessage);
                break;
            }
        }

    }
    private void handleUnsubscribe(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(String.valueOf(UNSUBSCRIBED_START));
        sendMessage.setReplyMarkup(getSubscribeKeyboard());

        bot.sendMessage(sendMessage);
    }
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        if (!callbackQuery.getData().contains("_")){
            log.error("Неопознанный запрос");
            return;
        }
        String chatId = callbackQuery.getMessage().getChatId().toString();
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(callbackQuery.getMessage().getMessageId())
                .text("")
                .build();
        SendMessage newMessage = null;

        String[] pages = callbackQuery.getData().split("_");
        Optional<CallbackData> lastPage = CallbackData.fromValue(pages[pages.length-1]);
        if (lastPage.isEmpty()) {
            if (isNumeric((pages[pages.length-1]))) {
                EditMessageText result = handleInteger(pages, callbackQuery);
                editMessage.setText(result.getText());
                editMessage.setReplyMarkup(result.getReplyMarkup());
            }
            else {
                log.error("Ошибочный Callback");
                return;
            }
        } else {
            switch (lastPage.get()) {
                case START_SUBSCRIBED -> {
                    if (isUserSubscribed(callbackQuery.getFrom().getId())) {
                        editMessage.setText(String.valueOf(AFTER_SUBSCRIBE));
                        newMessage = SendMessage.builder()
                                .chatId(chatId)
                                .replyMarkup(getStartKeyboard())
                                .text(String.valueOf(SUBSCRIBED_START))
                                .build();
                    } else {
                        editMessage.setText(String.valueOf(REPEATED_UNSUBSCRIBED));
                        editMessage.setReplyMarkup(getSubscribeKeyboard());
                    }
                }
                case PHONES_PAGE -> {
                    EditMessageText page = getAvailabilityPage();
                    editMessage.setText(page.getText());
                    editMessage.setReplyMarkup(page.getReplyMarkup());
                }
                case NEW_PAGE, USED_PAGE -> {
                    EditMessageText phonesPage = getPhonesPage(callbackQuery.getData());
                    editMessage.setText(phonesPage.getText());
                    editMessage.setReplyMarkup(phonesPage.getReplyMarkup());
                }
                default -> {
                    log.warn("Необработанный callback: {}", lastPage);
                    return;
                }
            }

        }

        bot.sendEditedMessage(editMessage);
        bot.sendMessage(newMessage);
        bot.sendCallbackAnswer(callbackQuery.getId());
    }
    private EditMessageText handleInteger(String[] pages, CallbackQuery callbackQuery) {
        EditMessageText message = new EditMessageText();
        String model = pages[3];
        String text = CHOICE_TEXT + model;
        switch (pages.length) {
            // Выбрано поколение
            case 4 -> {
                text += TYPE_TEXT.toString();
                message.setText(text);
                message.setReplyMarkup(getModelsKeyboard(model, callbackQuery.getData()));
            }
            // Выбран Pro/Pro Max и т.д.
            case 5 -> {
                String type = pages[4];
                text += " " + type + MEMORY_TEXT;
                message.setText(text + type);
                //message.setReplyMarkup(getMemoryKeyboard(model, callbackQuery.getData()));
            }
            // Выбрана память
            case 6 -> {
                String type = pages[4];
                String memory = pages[5];
                message.setText(text + type + memory);
//                message.setReplyMarkup();
//                if (Objects.equals(pages[pages.length - 2], USED_PAGE.toString())) {
//
//                } else if (Objects.equals(pages[pages.length - 2], NEW_PAGE.toString())) {
//
//                }
            }
        }
        return message;
    }

    // Keyboards
    private InlineKeyboardMarkup getSubscribeKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton buttonLink = InlineKeyboardButton.builder()
                .url(getInviteLink())
                .text(CHANNEL_LINK.toString())
                //.callbackData("subscribeLink")
                .build();
        row.add(buttonLink);
        row.add(getInlineKeyboardButton(SUBSCRIBED.toString(), ACTION + START_SUBSCRIBED.toString()));

        markup.setKeyboard(List.of(row));
        return markup;
    }
    private ReplyKeyboardMarkup getStartKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .build();
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(setKeyboardRow("Накопить кэшбек", "Списать баллы"));
        rows.add(setKeyboardRow("Узнать наличие", "Правила кэшбека"));

        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }
    private InlineKeyboardMarkup getPhonesKeyboard(String pageHistory) {
        List<String> models = phones.stream()
                .map(PhoneDto::getModel)
                .toList();
        return getGridKeyboard(PHONE_BUTTON.toString(), models, pageHistory);
    }
    private InlineKeyboardMarkup getModelsKeyboard(String generation, String callbackData) {
        PhoneDto phone = getPhoneByModel(generation);
        InlineKeyboardMarkup keyboardMarkup = null;

        if (phone != null && !phone.getType().isEmpty()) {
            List<String> types = phone.getType();
            String prefix = PHONE_BUTTON + generation + " ";
            keyboardMarkup = getGridKeyboard(prefix, types, callbackData);

        }
        return keyboardMarkup;
    }
    private InlineKeyboardMarkup getGridKeyboard(String prefixText, List<String> elements, String callbackData) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int totalTypes = elements.size();
        int index = 0;

        // Обработка первого ряда для нечётного количества
        if (totalTypes % 2 != 0) {
            List<InlineKeyboardButton> firstRow = new ArrayList<>();
            firstRow.add(getInlineKeyboardButton(prefixText + elements.get(index), callbackData, elements.get(index++)));
            buttons.add(firstRow);
        }

        // Обработка оставшихся кнопок по 2 в ряду
        while (index < totalTypes) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(getInlineKeyboardButton(prefixText + elements.get(index), callbackData, elements.get(index++)));
            if (index < totalTypes) {
                row.add(getInlineKeyboardButton(prefixText + elements.get(index), callbackData, elements.get(index++)));
            }
            buttons.add(row);
        }
        buttons.add(getButtonBack(callbackData));
        keyboard.setKeyboard(buttons);

        return keyboard;
    }

    // Elements for keyboards
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
    private static InlineKeyboardButton getInlineKeyboardButton(String text, String callbackData, String newPage) {
        if (newPage.isEmpty()) {
            newPage = COMMON_MODEL.toString();
        }
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData + "_" + newPage)
                .build();
    }
    private List<InlineKeyboardButton> getButtonBack(String pageHistory) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text(BACK_BUTTON.toString())
                .callbackData(pageHistory.substring(0,pageHistory.lastIndexOf("_")))
                .build();
        row.add(button);
        return row;
    }

    // Pages
    private SendMessage getStartPage(Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(String.valueOf(SUBSCRIBED_START))
                .replyMarkup(getStartKeyboard())
                .build();
    }
    private EditMessageText getAvailabilityPage() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        String callback = PAGE + PHONES_PAGE.toString() + "_";
        buttons.add(getInlineKeyboardButton(NEW_BUTTON.toString(), callback + NEW_PAGE));
        buttons.add(getInlineKeyboardButton(USED_BUTTON.toString(), callback + USED_PAGE));
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(List.of(buttons))
                .build();
        return EditMessageText.builder()
                .text(PHONES_TEXT.toString())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
    private EditMessageText getPhonesPage(String pageHistory) {
        InlineKeyboardMarkup keyboardMarkup = getPhonesKeyboard(pageHistory);
        return EditMessageText.builder()
                .text(MODEL_TEXT.toString())
                .replyMarkup(keyboardMarkup)
                .build();
    }

    // Functions
    private void initPhones() {
        List<PhoneDto> list = new ArrayList<>();
        try {
            list = dataStorageService.getPhones();
            log.debug("Запрос к сервису БД выполнен");
        } catch (RetryableException e) {
            log.error("Ошибка запроса к сервису БД");
        }
        if (!list.isEmpty()){
            Comparator<PhoneDto> compareByReleaseYear = Comparator
                    .comparing(PhoneDto::getReleaseYear);
            phones = list.stream()
                    .sorted(compareByReleaseYear.reversed())
                    .collect(Collectors.toList());
            log.debug("Список телефонов отсортирован");
        }

    }
    private String getInviteLink() {
        try {
            // Проверяем наличие у бота прав администратора
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
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    private PhoneDto getPhoneByModel(String model) {
        return phones.stream()
                .filter(phoneDto -> model.equals(phoneDto.getModel()))
                .findAny()
                .orElse(null);
    }
}