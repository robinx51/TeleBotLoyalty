package ru.onec.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.library.dto.PhoneDto;
import ru.library.dto.User1CRequestDto;
import ru.onec.service.OneCService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oneC")
public class OneCController {
    private final OneCService service;

    @GetMapping("/ping")
    public boolean ping() {
        return service.pingOneC();
    }
    @GetMapping("/getPhones")
    public Map<String, List<PhoneDto>> getPhones() {
        return service.getPhonesFrom1C();
    }

    @PostMapping("/addUser")
    public boolean addUser(@RequestBody User1CRequestDto user){
        return service.addUser(user);
    }
}
