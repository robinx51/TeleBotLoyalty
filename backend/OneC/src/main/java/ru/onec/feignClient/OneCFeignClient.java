package ru.onec.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.onec.configuration.OneCConfig;
import ru.onec.dto.PhoneResponseDto;
import ru.library.dto.User1CRequestDto;
import ru.onec.dto.UserResponseDto;

@FeignClient(name = "oneCStorage", url = "${1c.url.base}", configuration = OneCConfig.class)
public interface OneCFeignClient {
    @GetMapping("${serviceUrl.1c.ping}")
    String ping();
    @GetMapping("${serviceUrl.1c.getPhones}")
    PhoneResponseDto getPhones();

    @PostMapping("${serviceUrl.1c.addUser}")
    UserResponseDto addUser(User1CRequestDto user);
}