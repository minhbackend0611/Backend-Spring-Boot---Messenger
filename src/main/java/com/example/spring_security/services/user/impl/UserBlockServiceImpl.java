package com.example.spring_security.services.user.impl;

import com.example.spring_security.dto.request.RemoveBlockRequest;
import com.example.spring_security.dto.response.UserBlockResponse;
import com.example.spring_security.entities.Block;
import com.example.spring_security.entities.BlockId;
import com.example.spring_security.entities.Friend;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.BlockRepository;
import com.example.spring_security.repository.FriendRepository;
import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.services.user.UserBlockService;
import com.example.spring_security.services.user.UserFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBlockServiceImpl implements UserBlockService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserFriendService userFriendService;
    private final BlockRepository blockRepository;

    public Map<String, String> blockRequest(Long blockerId, Long blockedUserId) {

        if (blockerId.equals(blockedUserId)) {
            throw new CustomException(HttpStatus.CONFLICT, "Illegal behavior. You can not block yourself.");
        }

        if (!userRepository.existsById(blockerId) || !userRepository.existsById(blockedUserId))
            throw new RuntimeException("User no longer exists.");

        Friend friend = friendRepository.findExistingFriendBetween(blockerId, blockedUserId)
                .orElse(null);

        if (friend != null) userFriendService.removeFriend(blockerId, blockedUserId);

        Block block = blockRepository.findExistingBlock(blockerId, blockedUserId)
                .orElse(null);

        if (block != null) throw new CustomException(HttpStatus.CONFLICT, "You have already blocked this user.");

        BlockId blockId = BlockId.builder()
                .blockerId(blockerId)
                .blockedUserId(blockedUserId)
                .blockedAt(LocalDateTime.now())
                .build();

        block = Block.builder()
                .blockId(blockId)
                .blockedAt(blockId.getBlockedAt())
                .blocker(userRepository.findById(blockerId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User no longer exists.")))
                .blockedUser(userRepository.findById(blockedUserId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User no longer exists.")))
                .isActive(true)
                .build();
        blockRepository.save(block);
        Map<String, String> msg = new HashMap<>();
        msg.put("message", "You have successfully blocked this user.");
        return msg;
    }

    public List<UserBlockResponse> getBlockList(Long blockerId) {

        if (!userRepository.existsById(blockerId))
            throw new CustomException(HttpStatus.NOT_FOUND, "User no longer exists.");

        List<Block> blockList = blockRepository.findAllOf(blockerId);
        List<UserBlockResponse> userBlockResponseList = blockList.stream()
                .map(b -> UserBlockResponse.builder()
                        .userId(b.getBlockedUser().getUserId())
                        .avatarUrl(b.getBlockedUser().getAvatarUrl())
                        .username(b.getBlockedUser().getUsername())
                        .firstName(b.getBlockedUser().getFirstName())
                        .lastName(b.getBlockedUser().getLastName())
                        .blockedAt(b.getBlockedAt())
                        .build())
                .collect(Collectors.toList());
        return userBlockResponseList;
    }

    public Map<String, String> removeRequest(RemoveBlockRequest removeBlockRequest) {
        if (!userRepository.existsById(removeBlockRequest.getBlockerId()))
            throw new CustomException(HttpStatus.NOT_FOUND, "User no longer exists.");

        Block block = blockRepository.findBlock(
                removeBlockRequest.getBlockerId(),
                removeBlockRequest.getBlockedUserId(),
                removeBlockRequest.getBlockedAt()
        ).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User is not currently blocked."));

        block.setRemovedAt(LocalDateTime.now());
        block.setActive(false);
        blockRepository.save(block);

        Map<String, String> msg = new HashMap<>();
        msg.put("message", "You have successfully unblocked this user.");

        return msg;
    }
}
