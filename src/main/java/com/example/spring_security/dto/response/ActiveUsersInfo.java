package com.example.spring_security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUsersInfo {
    private Integer avgActivitiesMonthly;
    private String highestMonth;
    private String activityPercentage;
    private String trend;
}