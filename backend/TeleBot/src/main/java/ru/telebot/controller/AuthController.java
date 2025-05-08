package ru.telebot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.telebot.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/check-auth")
    public boolean checkAuth() {
        return authService.checkAuth("", "");
    }
}
