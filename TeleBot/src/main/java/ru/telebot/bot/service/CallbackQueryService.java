package ru.telebot.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.telebot.bot.TelegramBot;
import ru.telebot.bot.enums.CallbackData;
import ru.telebot.bot.enums.ScriptMessage;

import java.util.Optional;

//@Service
//@Slf4j
//@RequiredArgsConstructor
public class CallbackQueryService {

}
