package com.alpha.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SystemPropertiesModel {
    private String code;
    private String description;
    private String type;
    @NotNull(message = "ActionType should not be null")
    @NotBlank(message = "ActionType should not be empty")
    private String actionType;
}
