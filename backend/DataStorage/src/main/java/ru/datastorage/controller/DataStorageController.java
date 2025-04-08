package ru.datastorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.datastorage.db.entity.Phone;
import ru.datastorage.db.entity.User;
import ru.datastorage.service.DataStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class DataStorageController {
    private final DataStorageService dataStorageService;

    @PostMapping("/phones/addNew")
    public void addNew(@RequestBody @Validated Phone phone) {
        dataStorageService.addNewPhone(phone);
    }
    @GetMapping("/phones/getAll")
    public List<Phone> getAllPhones() {
        return dataStorageService.getAllPhones();
    }

    @PostMapping("/users/addNew")
    public void addNew(@RequestBody @Validated User user) {
        dataStorageService.addNewUser(user);
    }
    @GetMapping("/users/getAll")
    public List<User> getAllUsers() {
        return dataStorageService.getAllUsers();
    }
    @PostMapping("/users/update")
    public void updateUser(@RequestBody User user) {
        dataStorageService.updateUser(user);
    }
}
