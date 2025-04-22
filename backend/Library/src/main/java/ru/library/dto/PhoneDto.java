package ru.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PhoneDto {
    @Schema(name = "model", example = "15")
    private String model;

    @Schema(name = "type", example = "Pro Max")
    private String type;

    @Schema(name = "price", example = "116990")
    private int price;

    @Schema(name = "phone", example = "IPhone 15 Pro Max 256Gb")
    private String fullName;

    @Schema(name = "color", example = "Black")
    private List<String> color;
}