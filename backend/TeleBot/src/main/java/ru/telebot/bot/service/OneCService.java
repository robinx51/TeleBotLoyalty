package ru.telebot.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.library.dto.PhoneDto;
import ru.library.dto.User1CRequestDto;
import ru.telebot.feignClient.OneCFeignClient;

import java.util.List;
import java.util.Map;

@Service @Slf4j
@RequiredArgsConstructor
public class OneCService {
    private final OneCFeignClient feignClient;

    public Map<String, List<PhoneDto>> getPhones() {
        return feignClient.getPhones();
    }

    public boolean addUser(User1CRequestDto user) {
        return feignClient.addUser(user);
    }
}