package ru.telebot.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.telebot.dto.LoginRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RefreshScope
@RequiredArgsConstructor
public class AuthService {
    private final DataStorageService dataStorageService;
    private final List<UUID> tokens;

    @Value("${bot.secure}")
    private String secure;

    public ResponseEntity<?> login(LoginRequest request, HttpServletResponse response) {
        try {
            if (isValidCredentials(request)) {
                Cookie authCookie = new Cookie("authTokenSmartStore", generateToken());
                authCookie.setHttpOnly(true);
                authCookie.setPath("/");
                authCookie.setMaxAge(24 * 60 * 60); // 1 день
                authCookie.setSecure(secure.equals("true"));
                response.addCookie(authCookie);

                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authTokenSmartStore".equals(cookie.getName())) {
                    tokens.remove(UUID.fromString(cookie.getValue()));
                }
            }
        }

        Cookie cookie = new Cookie("authTokenSmartStore", null);
        cookie.setMaxAge(0); // Удаляем куку
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
    public ResponseEntity<?> validate(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authTokenSmartStore".equals(cookie.getName()) &&
                        validateToken(cookie.getValue())) {
                    return ResponseEntity.ok().build();
                }
            }
        }
        return ResponseEntity.status(401).build();
    }

    public void dropTokens() {
        tokens.clear();
    }

    public boolean isValidCredentials(LoginRequest request) {
        return dataStorageService.checkAdmin(request.getUsername(), request.getPassword());
    }
    private String generateToken() {
        String token = UUID.randomUUID().toString();
        tokens.add(UUID.fromString(token));
        return token;
    }
    private boolean validateToken(String token) {
        if (token != null && !token.isEmpty()) {
            return tokens.contains(UUID.fromString(token));
        }
        return false;
    }
}
