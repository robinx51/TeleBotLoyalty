package ru.telebot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PhoneDto {
    @NotNull
    @Schema(name = "model", example = "15")
    private String model;
    @NotNull
    @Schema(name = "releaseYear", example = "2025")
    private Integer releaseYear;
    @NotNull
    @Schema(name = "type", example = "[\"Common\",\"Pro Max\"]")
    private List<String> type;
    @NotNull
    @Schema(name = "memory", example = "[\"128\",\"256\",\"512\"]")
    private List<Integer> memory;
}
