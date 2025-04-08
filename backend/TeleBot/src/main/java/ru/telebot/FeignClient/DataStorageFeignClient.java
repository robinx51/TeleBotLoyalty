package ru.telebot.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.telebot.dto.PhoneDto;
import ru.telebot.dto.UserDto;

import java.util.List;

@FeignClient(name = "dataStorage", url = "${dataStorage.url}")
public interface DataStorageFeignClient {
    @PostMapping("/storage/phones/addNew")
    void addNewPhone(@RequestBody PhoneDto phone);
    @GetMapping("/storage/phones/getAll")
    List<PhoneDto> getPhones();

    @GetMapping("/storage/users/getAll")
    List<UserDto> getUsers();
    @PostMapping("/storage/users/addNew")
    void newUser(UserDto user);
    @PostMapping("/storage/users/update")
    void updateUser(UserDto user);
}