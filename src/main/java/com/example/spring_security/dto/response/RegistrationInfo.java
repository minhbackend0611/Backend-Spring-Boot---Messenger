package com.example.spring_security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationInfo {
    private Long totalRegistration;
    private Integer avgMonthly;
    private String highestMonth;
    private String growthPercentage;
}
