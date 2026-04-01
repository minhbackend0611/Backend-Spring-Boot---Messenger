package com.example.spring_security.services.user;

import com.example.spring_security.dto.request.UpdateStatusFriendRequest;
import com.example.spring_security.dto.response.FriendRequestResponse;
import com.example.spring_security.dto.response.ListUserFriendResponse;
import com.example.spring_security.dto.response.UserFriendResponse;

import java.util.List;
import java.util.Map;

public interface UserFriendService {
    ListUserFriendResponse getFriendList(Long userId, String keyword);

    Map<String, String> friendRequest(Long senderId, Long receiverId);

    List<FriendRequestResponse> getListFriendRequestReceive(Long id, String keyword);

    List<FriendRequestResponse> getListFriendRequestSent(Long id, String keyword);

    Map<String, String> updateStatus(UpdateStatusFriendRequest updateStatusFriendRequest, Long updatorId);

    Map<String, String> removeFriend(Long removerId, Long removedUserId);
}
