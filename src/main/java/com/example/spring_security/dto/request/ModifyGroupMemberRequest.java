package com.example.spring_security.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ModifyGroupMemberRequest {
    List<Long> memberIds;
}

