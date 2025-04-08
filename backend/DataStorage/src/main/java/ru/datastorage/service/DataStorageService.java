package ru.datastorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.datastorage.db.entity.Phone;
import ru.datastorage.db.entity.User;
import ru.datastorage.db.service.PhonesServiceDB;
import ru.datastorage.db.service.UsersServiceDB;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataStorageService {
    private final PhonesServiceDB phonesServiceDB;
    private final UsersServiceDB usersServiceDB;

    public void addNewPhone(Phone phone) {
        phonesServiceDB.addPhone(phone);
    }
    public List<Phone> getAllPhones() {
        return phonesServiceDB.getAll();
    }

    public void addNewUser(User user) {
        usersServiceDB.addUser(user);
    }
    public List<User> getAllUsers() {
        return usersServiceDB.getAll();
    }
    public void updateUser(User user) {
        usersServiceDB.updateUser(user);
    }
}
