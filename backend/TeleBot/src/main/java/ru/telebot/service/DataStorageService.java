package ru.telebot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.telebot.feignClient.DataStorageFeignClient;
import ru.library.dto.UserDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataStorageService {
    private String username;
    private String password;
    private final DataStorageFeignClient dataStorageFeignClient;

    public List<UserDto> getUsers() {
        log.debug("Получение списка покупателей из PG");
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

    public void getAdmin() {
        List<String> data = dataStorageFeignClient.getAdmin();
        username = data.getFirst();
        password = data.getLast();
    }
    public void updateAdmin(String username, String password) {
        dataStorageFeignClient.updateAdmin(new ArrayList<>(Arrays.asList(username, password)));
        this.username = username;
        this.password = password;
    }
    public boolean checkAdmin(String username, String password) {
        return this.username.equals(username) &&
                this.password.equals(password);
    }
    public String getAdminText() {
        return ( "Логин: " + username + "\nПароль: " + password);
    }
}
