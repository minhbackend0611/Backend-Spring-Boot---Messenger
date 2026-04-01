package com.example.spring_security.repository;

import com.example.spring_security.dto.response.GroupConversationMessageResponse;
import com.example.spring_security.entities.GroupConversationMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupConversationMessageRepository
    extends JpaRepository<GroupConversationMessage, Long> {

  @Query("""
      SELECT new com.example.spring_security.dto.response.GroupConversationMessageResponse(
        gcm.groupConversation.groupConversationId,
        gcm.sender.userId,
        TRIM(CONCAT(gcm.sender.firstName, ' ', gcm.sender.lastName)),
        gcm.sender.avatarUrl,
        gcm.groupConversationMessageId,
        gcm.content,
        gcm.sentAt,
        gcm.updatedAt,
        gcm.type
      )
      FROM GroupConversationMessage gcm
      WHERE gcm.groupConversation.groupConversationId = :groupConversationId
      AND (:cursorId IS NULL OR gcm.groupConversationMessageId < :cursorId)
      AND gcm.sentAt > :clearTime
      AND NOT EXISTS (
           SELECT 1 FROM DeleteGroupConversationMessage dgcm
           WHERE dgcm.id.groupConversationMessageId = gcm.groupConversationMessageId
           AND (dgcm.isAll = TRUE OR dgcm.id.memberId = :userId)
      )
      ORDER BY gcm.groupConversationMessageId DESC
      """)
  List<GroupConversationMessageResponse> findMessagesAfterTimestamp(
      @Param("userId") Long userId,
      @Param("groupConversationId") Long groupConversationId,
      @Param("cursorId") Long cursorId,
      @Param("clearTime") LocalDateTime clearTime,
      Pageable pageable);

  @Query("""
      SELECT new com.example.spring_security.dto.response.GroupConversationMessageResponse(
        gcm.groupConversation.groupConversationId,
        gcm.sender.userId,
        TRIM(CONCAT(gcm.sender.firstName, ' ', gcm.sender.lastName)),
        gcm.sender.avatarUrl,
        gcm.groupConversationMessageId,
        gcm.content,
        gcm.sentAt,
        gcm.updatedAt,
        gcm.type
      )
      FROM GroupConversationMessage gcm
      WHERE gcm.groupConversation.groupConversationId = :groupId
      AND gcm.groupConversationMessageId <= :messageId
      AND gcm.sentAt > :clearTime
      AND NOT EXISTS (
            SELECT 1 FROM DeleteGroupConversationMessage dgcm
            WHERE dgcm.id.groupConversationMessageId = gcm.groupConversationMessageId
            AND (dgcm.isAll = TRUE OR dgcm.id.memberId = :userId)
      )
      ORDER BY gcm.groupConversationMessageId DESC
      """)
  List<GroupConversationMessageResponse> findMessagesBeforeInclusive(
      @Param("userId") Long userId,
      @Param("groupId") Long groupId,
      @Param("messageId") Long messageId,
      @Param("clearTime") LocalDateTime clearTime,
      Pageable pageable);

  // 2. Fetch Newer Messages (Scrolling Down context)
  @Query("""
      SELECT new com.example.spring_security.dto.response.GroupConversationMessageResponse(
        gcm.groupConversation.groupConversationId,
        gcm.sender.userId,
        TRIM(CONCAT(gcm.sender.firstName, ' ', gcm.sender.lastName)),
        gcm.sender.avatarUrl,
        gcm.groupConversationMessageId,
        gcm.content,
        gcm.sentAt,
        gcm.updatedAt,
        gcm.type
      )
      FROM GroupConversationMessage gcm
      WHERE gcm.groupConversation.groupConversationId = :groupId
      AND gcm.groupConversationMessageId > :messageId
      AND gcm.sentAt > :clearTime
      AND NOT EXISTS (
            SELECT 1 FROM DeleteGroupConversationMessage dgcm
            WHERE dgcm.id.groupConversationMessageId = gcm.groupConversationMessageId
            AND (dgcm.isAll = TRUE OR dgcm.id.memberId = :userId)
      )
      ORDER BY gcm.groupConversationMessageId ASC
      """)
  List<GroupConversationMessageResponse> findMessagesAfter(
      @Param("userId") Long userId,
      @Param("groupId") Long groupId,
      @Param("messageId") Long messageId,
      @Param("clearTime") LocalDateTime clearTime,
      Pageable pageable);

}