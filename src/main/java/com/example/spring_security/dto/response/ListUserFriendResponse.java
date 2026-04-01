package com.example.spring_security.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListUserFriendResponse {
    private List<UserFriendResponse> listOfFriend;
    private Long count;
}
