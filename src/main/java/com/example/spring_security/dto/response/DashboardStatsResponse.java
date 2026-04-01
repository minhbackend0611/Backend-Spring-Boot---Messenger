package com.example.spring_security.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardStatsResponse {
    private RegistrationStats registration;
    private ActiveUsersStats activeUsers;
}
