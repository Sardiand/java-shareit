package ru.practicum.shareit.request.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
public class ItemRequestDto {
    @NotBlank
    private String description;
}