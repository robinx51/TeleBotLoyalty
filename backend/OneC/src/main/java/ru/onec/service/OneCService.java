package ru.onec.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.library.dto.ColorDto;
import ru.library.dto.PhoneDto;
import ru.onec.dto.PhoneResponseDto;
import ru.library.dto.User1CRequestDto;
import ru.onec.dto.UserResponseDto;
import ru.onec.feignClient.OneCFeignClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service @Slf4j
@RequiredArgsConstructor
public class OneCService {
    private final OneCFeignClient feignClient;

    @PostConstruct
    private void init() {
        if (pingOneC())
            log.info("1C server is available");
        else
            log.error("1C server not available");
    }

    public boolean pingOneC() {
        return feignClient.ping().equals("Working");
    }

    public Map<String, List<PhoneDto>> getPhonesFrom1C() {
        PhoneResponseDto response;
        try {
            response = feignClient.getPhones();
        } catch (Exception e) {
            log.error("Ошибка при запросе к 1С {}", e.getMessage());
            throw new RuntimeException(e);
        }

        if (!response.isSuccess()) {
            throw new RuntimeException("1C returned error: " + response.getError());
        }
        Map<String, List<PhoneDto>> result = new HashMap<>();

        result.put("new", processPhoneList(response.getData().getNewPhones()));
        result.put("used", processPhoneList(response.getData().getUsedPhones()));

        return result;
    }

    public boolean addUser(User1CRequestDto user) {
        UserResponseDto response = feignClient.addUser(user);
        if (response.isSuccess() && response.getCounteragent().getFullName().equals(user.getFullName())) {
            log.debug("Покупатель успешно добавлен в базу 1С");
            return true;
        } else {
            log.error("Ошибка при добавлении покупателя в базу 1с: {}", response);
            return false;
        }
    }

    private List<PhoneDto> processPhoneList(List<PhoneResponseDto.PhoneData.PhoneItem> phoneDataList) {
        return phoneDataList.stream()
                .map(this::convertToPhoneDto)
                .collect(Collectors.toList());
    }

    private PhoneDto convertToPhoneDto(PhoneResponseDto.PhoneData.PhoneItem phoneItem) {
        String processedPhone = phoneItem.getPhone().replaceFirst("^Смартфон Apple ", "");

        String[] parts = processedPhone.split(" ");
        String model = "";
        String type = "";

        if (parts.length > 0 && parts[0].equalsIgnoreCase("iPhone") && parts.length > 1) {
            model = parts[1];
        }

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase("Pro")) {
                if (i < parts.length - 1 && parts[i+1].equalsIgnoreCase("Max")) {
                    type = "Pro Max";
                } else {
                    type = "Pro";
                }
                break;
            } else if (parts[i].equalsIgnoreCase("Plus")) {
                type = "Plus";
                break;
            } else if (parts[i].equalsIgnoreCase("mini")) {
                type = "mini";
            }
        }

        List<ColorDto> colors = phoneItem.getColors();

        for (ColorDto color : colors) {
            List<String> stores = new ArrayList<>();
            for (String store : color.getStores()) {
                stores.add(store.replace("Склад ", ""));
            }
            color.setStores(stores);
        }

        return PhoneDto.builder()
                .model(model)
                .type(type.isEmpty() ? "" : type)
                .price(phoneItem.getPrice())
                .fullName(processedPhone)
                .color(colors)
                .build();
    }
}
