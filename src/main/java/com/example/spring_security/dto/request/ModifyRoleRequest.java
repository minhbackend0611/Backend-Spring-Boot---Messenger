package com.example.spring_security.dto.request;

import com.example.spring_security.entities.Enum.GroupRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyRoleRequest {
    private Long memberId;
    @Min(value = 0, message = "Invalid role.")
    @Max(value = 1, message = "Invalid role.")
    private Short groupRole;
}
