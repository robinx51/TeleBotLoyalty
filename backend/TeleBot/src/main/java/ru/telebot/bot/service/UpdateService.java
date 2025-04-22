package ru.telebot.bot.service;

import feign.RetryableException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.library.dto.User1CRequestDto;
import ru.telebot.bot.TelegramBot;
import ru.telebot.dto.UpdateUserDto;
import ru.telebot.enums.ButtonText;
import ru.telebot.enums.CallbackData;
import ru.library.dto.PhoneDto;
import ru.library.dto.UserDto;

import java.util.*;

import static ru.telebot.enums.ButtonText.*;
import static ru.telebot.enums.CallbackData.*;
import static ru.telebot.enums.ScriptMessage.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    /// TODO обработка после выбора типа
    private TelegramBot bot;
    private final DataStorageService dataStorageService;
    private final OneCService oneCService;

    @Value("@${bot.channelName}")
    private String channelName;
    private Map<String, List<PhoneDto>> phones;
    private Map<Long, UserDto> users;
    private Map<Integer, Long> usersIdByCode;

    public void registerBot(TelegramBot bot) {
        this.bot = bot;
        initUsers();
    }
    public List<UserDto> getUsers() {
        log.debug("Запрос списка пользователей");
        return users.values().stream().toList();
    }
    public UserDto getUserByCode(Integer code) throws NotFoundException {
        log.debug("Запрос пользователя с кодом: {}", code);
        return users.get(usersIdByCode.get(code));
    }
    public boolean updateUser(UpdateUserDto update) {
        log.debug("Обновление данных пользователя с кодом: {}", update.getCode());
        UserDto user = users.get(update.getTelegramId());
        if (user.getName() == null) {
            User1CRequestDto user1CRequestDto = User1CRequestDto.builder()
                    .fullName(update.getName())
                    .phone(update.getPhoneNumber())
                    .username(user.getUsername())
                    .build();
            if (!oneCService.addUser(user1CRequestDto))
                return false;

        }
        user.setName(update.getName());
        user.setPhoneNumber(update.getPhoneNumber());
        user.setCashback(calculateCashback(user.getCashback(), update));

        dataStorageService.updateUser(user);
        bot.sendMessage(getResponsePage(user));
        return true;
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
                log.debug("Получен текст: {}", update.getMessage().getText());
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
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(" ")
                .build();
        Optional<ButtonText> text = ButtonText.fromValue(update.getMessage().getText());
        if (text.isPresent()){
            switch (text.get()) {
                case ADD_CASHBACK -> {
                    Integer code = getCodeById(update.getMessage().getFrom().getId(), update.getMessage().getFrom());
                    message.setText(ADD_CASHBACK_RESPONSE.toString() + code);
                }
                case SUB_CASHBACK -> {
                    Integer code = getCodeById(update.getMessage().getFrom().getId(), update.getMessage().getFrom());
                    message.setText(SUB_CASHBACK_RESPONSE.toString() + code);
                }
                case AVAILABILITY -> {
                    EditMessageText availabilityPage = getAvailabilityPage();
                    message.setText(availabilityPage.getText());
                    message.setReplyMarkup(availabilityPage.getReplyMarkup());
                }
                case CASHBACK_RULES -> message.setText("Здесь будут правила кэшбека");
                default -> {
                    log.warn("Необработанный текст: {}", text.get());
                    message.setText("Неизвестная команда, попробуйте ещё раз");
                }
            }
        } else
            message.setText(UNKNOWN_COMMAND.toString());
        bot.sendMessage(message);
    }
    private void handleCommand(Message message) {
        switch (message.getText()) {
            case "/start" -> {
                User user = message.getFrom();
                if (!isUserSubscribed(user.getId())) {
                    handleUnsubscribe(message);
                } else {
                    bot.sendMessage(getStartPage(message.getChatId()));
                    if (!users.containsKey(user.getId()))
                        newUser(user);
                }
            } case "/help" -> {

            } default -> {
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(message.getChatId())
                        .text(String.valueOf(UNKNOWN_COMMAND))
                        .build();
                bot.sendMessage(sendMessage);
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
                    EditMessageText phonesPage = getPhonesPage(callbackQuery.getData(), lastPage.get().toString());
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
        String condition = pages[2];
        String text = CHOICE_TEXT + model;
        switch (pages.length) {
            // Выбрано поколение
            case 4 -> {
                text += TYPE_TEXT.toString();
                message.setText(text);
                message.setReplyMarkup(getModelsKeyboard(model, callbackQuery.getData(), condition));
            }
            // Выбран Pro/Pro Max и т.д.
            case 5 -> {
                String type = pages[4];
                //text += " " + type + MEMORY_TEXT;
                //message.setText(text + type);
                //message.setReplyMarkup(getMemoryKeyboard(model, callbackQuery.getData()));
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
        rows.add(setKeyboardRow(ADD_CASHBACK.toString(), SUB_CASHBACK.toString()));
        rows.add(setKeyboardRow(AVAILABILITY.toString(), CASHBACK_RULES.toString()));

        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }
    private InlineKeyboardMarkup getPhonesKeyboard(String pageHistory, String condition) {
        List<String> models = phones.get(condition).stream()
                .map(PhoneDto::getModel)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        return getGridKeyboard(PHONE_BUTTON.toString(), models, pageHistory);
    }
    private InlineKeyboardMarkup getModelsKeyboard(String generation, String callbackData, String condition) {
        List<String> typesByModel = getTypesByModel(generation, condition);
        InlineKeyboardMarkup keyboardMarkup = null;

        if (!typesByModel.isEmpty()) {
            String prefix = PHONE_BUTTON + generation + " ";
            keyboardMarkup = getGridKeyboard(prefix, typesByModel, callbackData);
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
    private EditMessageText getPhonesPage(String pageHistory, String condition) {
        InlineKeyboardMarkup keyboardMarkup = getPhonesKeyboard(pageHistory, condition);
        return EditMessageText.builder()
                .text(MODEL_TEXT.toString())
                .replyMarkup(keyboardMarkup)
                .build();
    }
    private SendMessage getResponsePage(UserDto user) {
        return SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(RESPONSE_TEXT.toString() + user.getCashback())
                .build();
    }

    // Functions
    @Async
    @Scheduled(initialDelay = 2000, fixedDelayString = "${interval.phones}") // Раз в сутки
    public void updatePhones() throws InterruptedException {
        Map<String, List<PhoneDto>> phoneMap = new HashMap<>();
        try {
            phoneMap = oneCService.getPhones();
            log.info("Список телефонов обновлён");
        } catch (RetryableException e) {
            log.error("Ошибка запроса к 1С - список телефонов не обновлён");
            Thread.sleep(60 * 1000); // Через минуту повторить
            updatePhones();
        }
        if (!phoneMap.isEmpty()){
            phones = phoneMap;
        }
    }
    private void initUsers() {
        List<UserDto> userList = new ArrayList<>();
        users = new HashMap<>();
        usersIdByCode = new HashMap<>();
        try {
            userList = dataStorageService.getUsers();
            log.debug("Запрос к сервису БД выполнен");
        } catch (RetryableException e) {
            log.error("Ошибка запроса к сервису БД");
        }
        if (!userList.isEmpty()) {
            for (UserDto user : userList) {
                users.put(user.getTelegramId(), user);
                usersIdByCode.put(user.getCode(), user.getTelegramId());
            }
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
    private void newUser(User user) {
        Long tgId = user.getId();
        Integer code = (int) (Math.random() * (99999 - 10000) + 10000);
        if (users.containsKey(tgId) && users.get(tgId).getCode().equals(code))
            newUser(user);
        UserDto userDto = UserDto.builder()
                .telegramId(tgId)
                .username(user.getUserName())
                .code(code)
                .cashback(0)
                .build();
        users.put(tgId, userDto);
        usersIdByCode.put(code, tgId);

        dataStorageService.newUser(userDto);
    }
    private Integer getCodeById(Long id, User user) {
        if (!users.containsKey(id))
            newUser(user);
        return users.get(id).getCode();
    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    private List<String> getTypesByModel(String model, String condition) {
        return phones.get(condition).stream()
                .filter(phoneDto -> model.equals(phoneDto.getModel()))
                .map(PhoneDto::getType)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }
    private int calculateCashback(int cashback, UpdateUserDto update) {
        int newCashback = cashback;
        switch (update.getAction()) {
            case "earn" ->
                newCashback += update.getOperationAmount();
            case "spend" -> {
                if (update.getOperationAmount() > cashback)
                    newCashback = 0;
                else
                    newCashback -= update.getOperationAmount();
            }
        }
        return newCashback;
    }
    private SendMessage getMessageWithLink(Long chatId, String text, String url, int startIndex, int length) {
        List<MessageEntity> entities = new ArrayList<>();
        LinkPreviewOptions options = new LinkPreviewOptions();

        entities.add(MessageEntity.builder()
                .type("text_link")
                .offset(startIndex)
                .length(length)
                .url(url)
                .build());
        options.setUrlField(url);

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .entities(entities)
                .linkPreviewOptions(options)
                .build();
    }
}