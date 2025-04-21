package ru.telebot.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.telebot.FeignClient.OneCStorageFeignClient;
import ru.telebot.configuration.OneCConfig;
import ru.telebot.dto.OneCPhoneResponseDto;
import ru.telebot.dto.PhoneDto;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OneCService {
    private final OneCStorageFeignClient feignClient;
    private final OneCConfig oneCConfig;

    public Map<String, List<PhoneDto>> getPhonesFrom1C() {
        HttpHeaders headers = createHeadersWithAuth();
        HttpEntity<?> request = new HttpEntity<>(headers);

        OneCPhoneResponseDto response = feignClient.getPhones();

        if (response.isSuccess()) {
            //return convertToPhoneDtos(response.getData());
        }
        throw new RuntimeException("Failed to get phones from 1C: " +
                (response.getError() != null ? response.getError() : "No response"));
    }

    private HttpHeaders createHeadersWithAuth() {
        String auth = oneCConfig.getLogin() + ":" + oneCConfig.getPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private List<PhoneDto> convertToPhoneDtos(OneCPhoneResponseDto response) {
        // Здесь нужно реализовать логику преобразования строк из 1С в PhoneDto
        // Это пример - адаптируйте под ваш реальный формат данных

        return response.getData().getNewPhones().stream()
                .map(this::parsePhoneString)
                .collect(Collectors.toList());
    }

    private PhoneDto parsePhoneString(String phoneStr) {
        // Пример парсинга строки "iPhone 15 Pro Max 2023 128GB"
        String[] parts = phoneStr.split(" ");

        return PhoneDto.builder()
                .model(parts[1]) // 15
                .releaseYear(Integer.parseInt(parts[3])) // 2023
                .type(List.of(parts[2])) // ["Pro", "Max"]
                .memory(List.of(parts[4].replace("GB", ""))) // ["128"]
                .build();
    }
}