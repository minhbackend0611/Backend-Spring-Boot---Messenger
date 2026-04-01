package com.example.spring_security.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ListRecordSignInResponse {

    private Long countSuccess;

    private Long countFailed;

    private Long total;

    private List<RecordSignInResponse> recordSignInResponseList;

}
