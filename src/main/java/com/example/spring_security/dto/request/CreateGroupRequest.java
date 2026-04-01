package com.example.spring_security.dto.request;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupRequest {

    private List<Long> userIds;

    private String groupName;

}
