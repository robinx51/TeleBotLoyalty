package ru.telebot.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.telebot.dto.OneCPhoneResponseDto;
import ru.telebot.dto.PhoneDto;
import ru.telebot.dto.UserDto;

import java.util.List;

@FeignClient(name = "oneCStorage", url = "${1c.url.base}")
public interface OneCStorageFeignClient {
    @GetMapping("${1c.url.ping}")
    OneCPhoneResponseDto ping();
    @GetMapping("${1c.url.getPhones}")
    OneCPhoneResponseDto getPhones();

    @PostMapping("${1c.url.addUser}")
    void addUser(UserDto user);
}