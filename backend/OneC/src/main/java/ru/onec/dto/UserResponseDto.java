package ru.onec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    @JsonProperty("Success")
    private boolean success;
    @JsonProperty("Counteragent")
    private Counteragent counteragent;

    @Data
    public static class Counteragent {
        @JsonProperty("FullName")
        private String fullName;
        @JsonProperty("Url")
        private String url;
    }
}