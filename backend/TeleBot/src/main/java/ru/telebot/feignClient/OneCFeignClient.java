package ru.telebot.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.library.dto.PhoneDto;
import ru.library.dto.User1CRequestDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "oneCStorage", url = "${serviceUrl.oneC}")
public interface OneCFeignClient {
    @GetMapping("/oneC/ping")
    String ping();
    @GetMapping("/oneC/getPhones")
    Map<String, List<PhoneDto>> getPhones();

    @PostMapping("/oneC/addUser")
    boolean addUser(User1CRequestDto user);
}