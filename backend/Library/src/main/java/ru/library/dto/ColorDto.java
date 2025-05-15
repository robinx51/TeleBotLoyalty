package ru.library.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ColorDto {
    private String name;
    @JsonProperty("store")
    private List<String> stores;
}
