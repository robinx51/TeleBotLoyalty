package ru.onec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.library.dto.ColorDto;

import java.util.List;

@Data
@Builder
public class PhoneResponseDto {
    private boolean success;
    private String error;
    private PhoneData data;

    @Data
    public static class PhoneData {
        @JsonProperty("new")
        private List<PhoneItem> newPhones;
        @JsonProperty("used")
        private List<PhoneItem> usedPhones;

        @Data
        public static class PhoneItem {
            private String phone;
            private int price;
            @JsonProperty("color")
            private List<ColorDto> colors;
        }
    }
}