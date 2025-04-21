package ru.telebot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OneCPhoneResponseDto {
    private boolean success;
    private String error;
    private PhoneData data;

    @Data
    public static class PhoneData {
        @JsonProperty("new")
        private List<String> newPhones;
        @JsonProperty("used")
        private List<String> usedPhones;
    }
}