package com.example.spring_security.repository;

import com.example.spring_security.dto.response.ConversationListItem;
import com.example.spring_security.entities.PrivateConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Long> {

    List<PrivateConversation> findAll();

    List<PrivateConversation> findByUser1UserIdOrUser2UserId(Long user1Id, Long user2Id);

    @Query(value = """
            SELECT *
            FROM private_conversation pc
            WHERE (
                    (pc.user1_id = :user1Id AND pc.user2_id = :user2Id)
                    OR
                    (pc.user1_id = :user2Id AND pc.user2_id = :user1Id)           
                  )
            """, nativeQuery = true)
    Optional<PrivateConversation> findByUser1UserIdAndUser2UserId(Long user1Id, Long user2Id);

    Optional<PrivateConversation> findById(Long privateConversationId);


    @Query(value = """
            WITH pc AS (
                SELECT
                    pc.private_conversation_id AS id,
                    pc.created_at AS conv_created_at,
            
                    CASE WHEN pc.user1_id = :userId
                         THEN CONCAT(u2.first_name, ' ', u2.last_name)
                         ELSE CONCAT(u1.first_name, ' ', u1.last_name)
                    END AS name,
            
                    CASE WHEN pc.user1_id = :userId
                         THEN u2.avatar_url
                         ELSE u1.avatar_url
                    END AS avatarUrl,
            
                    m.content AS previewContent,
                    m.type AS previewType,
                    m.sent_at AS previewTime,
            
                    dconv.deleted_at AS deletedAt
                FROM private_conversation pc
                JOIN user_info u1 ON u1.user_id = pc.user1_id
                JOIN user_info u2 ON u2.user_id = pc.user2_id
                LEFT JOIN private_conversation_message m
                    ON m.private_conversation_message_id = pc.preview_message_id
                LEFT JOIN delete_private_conversation dconv
                    ON dconv.private_conversation_id = pc.private_conversation_id
                   AND dconv.user_id = :userId
                WHERE :userId IN (pc.user1_id, pc.user2_id)
            ),
            
            filtered_pc AS (
                SELECT *,
                    COALESCE(previewTime, conv_created_at) AS effectiveTime
                FROM pc
            ),
            
            
            gc AS (
                SELECT
                    gc.group_conversation_id AS id,
                    gc.group_name AS name,
                    gc.avatar_url AS avatarUrl,
                    gc.created_at AS conv_created_at,
            
                    m.content AS previewContent,
                    m.type AS previewType,
                    m.sent_at AS previewTime,
            
                    dconv.deleted_at AS deletedAt
                FROM group_conversation gc
                JOIN group_conversation_member gm
                    ON gm.group_conversation_id = gc.group_conversation_id
                   AND gm.member_id = :userId
                LEFT JOIN group_conversation_message m
                    ON m.group_conversation_message_id = gc.preview_message_id
                LEFT JOIN delete_group_conversation dconv
                    ON dconv.group_conversation_id = gc.group_conversation_id
                   AND dconv.member_id = :userId
            ),
            
            filtered_gc AS (
                SELECT *,
                    COALESCE(previewTime, conv_created_at) AS effectiveTime
                FROM gc
            )
            
            SELECT
                CAST(0 AS SMALLINT) AS type,
                id,
                name,
                avatarUrl,
                previewContent,
                previewType,
                effectiveTime AS previewTime
            FROM filtered_pc
            
            UNION ALL
            
            SELECT
                CAST(1 AS SMALLINT) AS type,
                id,
                name,
                avatarUrl,
                previewContent,
                previewType,
                effectiveTime AS previewTime
            FROM filtered_gc
            
            ORDER BY previewTime DESC;
            """, nativeQuery = true)
    List<ConversationListItem> getConversationList(Long userId);
}
