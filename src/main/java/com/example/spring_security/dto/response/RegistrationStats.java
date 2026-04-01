package com.example.spring_security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStats {
    private List<Integer> dataByMonth;   // 12 giá trị
    private RegistrationInfo stats;       // object stats bên trong
}
