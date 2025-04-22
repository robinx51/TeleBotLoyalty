package ru.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class User1CRequestDto {
    private String fullName;
    private String phone;
    private String username;
}
