package ru.datastorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.datastorage.db.entity.Admin;
import ru.datastorage.db.entity.User;
import ru.datastorage.db.service.UsersServiceDB;

import java.util.ArrayList;
import java.util.Arrays;
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

    public List<String> getAdmin() {
        log.debug("Запрос к БД: getAdmin");
        Admin admin = usersServiceDB.getAdmin();
        return new ArrayList<>(Arrays.asList(admin.getUsername(), admin.getPassword()));
    }
    public void updateAdmin(List<String> adminStr) {
        log.debug("Запрос к БД: updateAdmin");
        Admin admin = usersServiceDB.getAdmin();
        admin.setUsername(adminStr.getFirst());
        admin.setPassword(adminStr.getLast());
        usersServiceDB.updateAdmin(admin);
    }
}
