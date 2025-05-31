package ru.telebot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.telebot.service.BotService;
import ru.telebot.dto.UpdateUserDto;
import ru.library.dto.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bot")
public class BotController {
    private final BotService botService;

    @GetMapping("/users/getAll")
    public List<UserDto> getUsers() {
        return botService.getUsers();
    }

    @GetMapping("/users/getByCode")
    public UserDto getUserByCode(@RequestParam Integer code) throws NotFoundException {
        return botService.getUserByCode(code);
    }

    @PostMapping("/users/update")
    public boolean updateUser(@RequestBody UpdateUserDto user, HttpServletRequest request) {
        return botService.updateUser(user, request);
    }
}
