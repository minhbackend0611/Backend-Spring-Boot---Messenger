package com.example.spring_security.repository;

import com.example.spring_security.dto.response.PrivateConversationMessageResponse;
import com.example.spring_security.entities.PrivateConversationMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrivateConversationMessageRepository extends JpaRepository<PrivateConversationMessage, Long> {

    @Query("""
            SELECT new com.example.spring_security.dto.response.PrivateConversationMessageResponse(
                pcm.privateConversation.privateConversationId,
                pcm.sender.userId,
                TRIM(CONCAT(pcm.sender.firstName, ' ', pcm.sender.lastName)),
                pcm.sender.avatarUrl,
                pcm.privateConversationMessageId,
                pcm.content,
                pcm.sentAt,
                pcm.updatedAt,
                pcm.type
            )
            FROM PrivateConversationMessage pcm
            WHERE pcm.privateConversation.privateConversationId = :privateConversationId
            AND (:cursorId IS NULL OR pcm.privateConversationMessageId < :cursorId)

            AND pcm.sentAt > :clearTime

            AND NOT EXISTS (
                  SELECT 1 FROM DeletePrivateConversationMessage dpcm
                  WHERE dpcm.id.privateConversationMessageId = pcm.privateConversationMessageId
                  AND (dpcm.isAll = TRUE OR dpcm.id.userId = :userId)
            )
            ORDER BY pcm.privateConversationMessageId DESC
            """)
    List<PrivateConversationMessageResponse> findMessagesAfterTimestamp(
            @Param("userId") Long userId,
            @Param("privateConversationId") Long privateConversationId,
            @Param("cursorId") Long cursorId,
            @Param("clearTime") LocalDateTime clearTime,
            Pageable pageable);

    // ... inside the interface ...

    @Query("""
            SELECT new com.example.spring_security.dto.response.PrivateConversationMessageResponse(
                pcm.privateConversation.privateConversationId,
                pcm.sender.userId,
                TRIM(CONCAT(pcm.sender.firstName, ' ', pcm.sender.lastName)),
                pcm.sender.avatarUrl,
                pcm.privateConversationMessageId,
                pcm.content,
                pcm.sentAt,
                pcm.updatedAt,
                pcm.type
            )
            FROM PrivateConversationMessage pcm
            WHERE pcm.privateConversation.privateConversationId = :privateConversationId
            AND pcm.privateConversationMessageId <= :messageId
            AND pcm.sentAt > :clearTime
            AND NOT EXISTS (
                  SELECT 1 FROM DeletePrivateConversationMessage dpcm
                  WHERE dpcm.id.privateConversationMessageId = pcm.privateConversationMessageId
                  AND (dpcm.isAll = TRUE OR dpcm.id.userId = :userId)
            )
            ORDER BY pcm.privateConversationMessageId DESC
            """)
    List<PrivateConversationMessageResponse> findMessagesBeforeInclusive(
            @Param("userId") Long userId,
            @Param("privateConversationId") Long privateConversationId,
            @Param("messageId") Long messageId,
            @Param("clearTime") LocalDateTime clearTime,
            Pageable pageable);

    @Query("""
            SELECT new com.example.spring_security.dto.response.PrivateConversationMessageResponse(
                pcm.privateConversation.privateConversationId,
                pcm.sender.userId,
                TRIM(CONCAT(pcm.sender.firstName, ' ', pcm.sender.lastName)),
                pcm.sender.avatarUrl,
                pcm.privateConversationMessageId,
                pcm.content,
                pcm.sentAt,
                pcm.updatedAt,
                pcm.type
            )
            FROM PrivateConversationMessage pcm
            WHERE pcm.privateConversation.privateConversationId = :privateConversationId
            AND pcm.privateConversationMessageId > :messageId
            AND pcm.sentAt > :clearTime
            AND NOT EXISTS (
                  SELECT 1 FROM DeletePrivateConversationMessage dpcm
                  WHERE dpcm.id.privateConversationMessageId = pcm.privateConversationMessageId
                  AND (dpcm.isAll = TRUE OR dpcm.id.userId = :userId)
            )
            ORDER BY pcm.privateConversationMessageId ASC
            """)
    List<PrivateConversationMessageResponse> findMessagesAfter(
            @Param("userId") Long userId,
            @Param("privateConversationId") Long privateConversationId,
            @Param("messageId") Long messageId,
            @Param("clearTime") LocalDateTime clearTime,
            Pageable pageable);
}