package com.example.spring_security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RenameGroupRequest {
    @NotBlank
    @Size(max = 100, message = "Group name must not exceed 100 characters.")
    private String groupName;
}
