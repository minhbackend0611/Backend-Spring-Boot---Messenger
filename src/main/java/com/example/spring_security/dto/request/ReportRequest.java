package com.example.spring_security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {
    @NotBlank(message = "Reason must not be blank.")
    @Size(max = 200, message = "Reason must not exceed 200 characters.")
    private String reason;
}
