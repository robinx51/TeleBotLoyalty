package ru.telebot.controller;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.telebot.bot.service.UpdateService;
import ru.telebot.dto.UpdateUserDto;
import ru.telebot.dto.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bot")
public class BotController {
    private final UpdateService updateService;

    @GetMapping("/users/getAll")
    public List<UserDto> getUsers() {
        return updateService.getUsers();
    }

    @GetMapping("/users/getByCode")
    public UserDto getUserByCode(@RequestParam Integer code) throws NotFoundException {
        return updateService.getUserByCode(code);
    }

    @PostMapping("/users/update")
    public void updateUser(@RequestBody UpdateUserDto user) {
        updateService.updateUser(user);
    }

    @PostMapping("/users/editUser")
    public boolean editUser(@RequestBody UserDto user) {
        return updateService.editUser(user);
    }
}
