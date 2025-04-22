package ru.telebot.controller;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.telebot.bot.service.UpdateService;
import ru.telebot.dto.UpdateUserDto;
import ru.library.dto.UserDto;

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
    public boolean updateUser(@RequestBody UpdateUserDto user) {
        return updateService.updateUser(user);
    }
}
