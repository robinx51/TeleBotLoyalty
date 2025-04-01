package ru.datastorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.datastorage.db.entity.Phone;
import ru.datastorage.service.DataStorageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class DataStorageController {
    private final DataStorageService dataStorageService;

    @PostMapping("/phones/addNew")
    public void addNew(@RequestBody @Validated Phone phone) {
        dataStorageService.addNewPhone(phone);
    }

}
