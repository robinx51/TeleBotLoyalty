package ru.telebot.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.telebot.dto.PhoneDto;

import java.util.List;

@FeignClient(name = "dataStorage", url = "${dataStorage.url}")
public interface DataStorageFeignClient {
    @PostMapping("/storage/phones/addNew")
    void addNewPhone(@RequestBody PhoneDto phone);
    @GetMapping("/storage/phones/getAll")
    List<PhoneDto> getPhones();
}