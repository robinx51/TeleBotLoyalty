package ru.telebot.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.telebot.FeignClient.DataStorageFeignClient;
import ru.telebot.dto.PhoneDto;
import ru.telebot.dto.UserDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataStorageService {
    private final DataStorageFeignClient dataStorageFeignClient;

    public List<PhoneDto> getPhones() {
        log.debug("Получение списка айфонов из PG");
        return dataStorageFeignClient.getPhones();
    }

    public List<UserDto> getUsers() {
        log.debug("Получение списка айфонов из PG");
        return dataStorageFeignClient.getUsers();
    }

    public void newUser(UserDto user) {
        log.debug("Добавление user в БД");
        dataStorageFeignClient.newUser(user);
    }

    public void updateUser(UserDto user) {
        log.debug("Обновление user с кодом: {}", user.getCode());
        dataStorageFeignClient.updateUser(user);
    }
}
