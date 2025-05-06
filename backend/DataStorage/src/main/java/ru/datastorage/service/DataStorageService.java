package ru.datastorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.datastorage.db.entity.User;
import ru.datastorage.db.service.UsersServiceDB;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataStorageService {
    private final UsersServiceDB usersServiceDB;

    public void addNewUser(User user) {
        log.debug("Запрос к БД: addNewUser");
        usersServiceDB.addUser(user);
    }
    public List<User> getAllUsers() {
        log.debug("Запрос к БД: getAllUsers");
        return usersServiceDB.getAll();
    }
    public void updateUser(User user) {
        log.debug("Запрос к БД: updateUser");
        usersServiceDB.updateUser(user);
    }
}
