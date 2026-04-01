package com.example.spring_security.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ListReportResponse {
    private List<ReportResponse> reportResponseList;
    private int count;
}
