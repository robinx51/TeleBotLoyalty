package ru.telebot.bot;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription;
import org.telegram.telegrambots.meta.api.methods.description.SetMyShortDescription;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.telebot.service.UpdateService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.telebot.enums.ScriptMessage.BOT_DESCRIPTION;
import static ru.telebot.enums.ScriptMessage.BOT_SHORT_DESCRIPTION;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final ExecutorService executorServiceForUpdates = Executors.newFixedThreadPool(5);
    private final ExecutorService executorServiceForSending = Executors.newFixedThreadPool(5);
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    @Value("${bot.name}")
    private String botUsername;
    private final UpdateService updateService;

    public TelegramBot(@Value("${bot.token}") String token, UpdateService updateService) {
        super(token);
        this.updateService = updateService;
    }

    @PostConstruct
    public void init() {
        updateService.registerBot(this);
        isBotRunning();

        SetMyCommands setMyCommands = SetMyCommands.builder()
                .commands(List.of(new BotCommand("start", "Начало взаимодействия с ботом")
                        , new BotCommand("help", "Информация о боте")))
                .build();
        SetMyDescription setMyDescription = SetMyDescription.builder()
                .description(String.valueOf(BOT_DESCRIPTION))
                .build();
        SetMyShortDescription setMyShortDescription = SetMyShortDescription.builder()
                .shortDescription(String.valueOf(BOT_SHORT_DESCRIPTION))
                .build();
        try {
            execute(setMyCommands);
            execute(setMyDescription);
            execute(setMyShortDescription);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        executorServiceForUpdates.submit(() -> updateService.handleUpdate(update));
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            executorServiceForSending.submit(() -> execute(message));
        }
    }

    public void sendEditedMessage(EditMessageText message) {
        if (message != null) {
            executorServiceForSending.submit(() -> execute(message));
        }
    }

    public void sendCallbackAnswer(String queryId) {
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(queryId)
                    .build());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    public void isBotRunning() {
        try {
            GetMe getMe = new GetMe();
            User botUser = execute(getMe);
            logger.info("Бот работает, username: {}", botUser.getUserName());
        } catch (TelegramApiException e) {
            logger.error("Бот не работает: {}", e.getMessage());
        }
    }
}