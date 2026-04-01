package com.example.spring_security.services.user;

import com.example.spring_security.dto.request.*;
import com.example.spring_security.dto.response.ListGroupConversationMessageResponse;
import com.example.spring_security.dto.response.SendMessageResponse;
import com.example.spring_security.dto.response.UserFriendResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserGroupConversationService {

        Map<String, Object> createGroup(Long userId, CreateGroupRequest createGroupRequest);

        ListGroupConversationMessageResponse getMessages(Long userId, Long groupConversationId, Long cursorId,
                        Long newerCursorId,
                        Long jumpToMessageId);

        SendMessageResponse sendMessage(Long userId, Long groupConversationId, SendMessageRequest sendMessageRequest);

        Map<String, String> removeMessage(Long userId, Long groupConversationId, Long groupConversationMessageId,
                        Boolean isAll);

        Map<String, String> clearGroupChatHistory(Long userId, Long groupConversationId);

        Map<String, String> removeConversation(Long removerId, Long groupConversationId);

        Map<String, String> renameConversation(Long userId, Long groupConversationId,
                        RenameGroupRequest renameGroupRequest);

        Map<String, String> updateAvatar(Long userId, Long groupConversationId, MultipartFile avatar);

        List<UserFriendResponse> list(Long userId, Long groupConversationId, String keyword);

        Map<String, String> addMembers(Long userId, Long groupConversationId,
                        ModifyGroupMemberRequest modifyGroupMemberRequest);

        Map<String, String> removeMembers(Long userId, Long groupConversationId,
                        ModifyGroupMemberRequest modifyGroupMemberRequest);

        Map<String, String> modifyRoleMembers(Long userId, Long groupConversationId,
                        ModifyRoleRequest modifyRoleRequest);

        Map<String, String> out(Long userId, Long groupConversationId);

        interface WebSocketSenderService {
        }
}
