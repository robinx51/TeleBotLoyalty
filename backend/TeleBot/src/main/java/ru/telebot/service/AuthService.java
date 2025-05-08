package ru.telebot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public boolean checkAuth(String username, String password) {
        return username.equals(adminUsername) && password.equals(adminPassword);
    }
}
