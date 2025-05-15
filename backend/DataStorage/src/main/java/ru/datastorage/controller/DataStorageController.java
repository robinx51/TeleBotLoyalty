package ru.datastorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.datastorage.db.entity.User;
import ru.datastorage.service.DataStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class DataStorageController {
    private final DataStorageService dataStorageService;

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

    @GetMapping("/admin/get")
    public List<String> getAdmin(){
        return dataStorageService.getAdmin();
    }
    @PostMapping("/admin/update")
    public void updateAdmin(@RequestBody List<String> admin) {
        dataStorageService.updateAdmin(admin);
    }
}