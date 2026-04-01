package com.example.spring_security.repository;

import com.example.spring_security.dto.response.GroupConversationItemListResponse;
import com.example.spring_security.entities.GroupConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupConversationRepository extends JpaRepository<GroupConversation, Long> {

    Optional<GroupConversation> findById(Long groupConversationId);


    @Query(value = """
    SELECT 
        gc.group_conversation_id AS groupConversationId,
        gc.avatar_url AS avatarUrl,
        gc.group_name AS groupName,
        gc.owner_id AS ownerId,
        gc.created_at AS createdAt,
        gc.is_encrypted AS isEncrypted,

        (SELECT ui.username 
         FROM user_info ui 
         WHERE ui.user_id = gc.owner_id
         LIMIT 1) AS ownerUsername,


        (SELECT COUNT(*) 
         FROM group_conversation_member gcm 
         WHERE gcm.group_conversation_id = gc.group_conversation_id) AS memberCount,

        (SELECT COUNT(*) 
         FROM group_conversation_member gcm2 
         WHERE gcm2.group_conversation_id = gc.group_conversation_id 
           AND gcm2.group_role = 1) AS adminCount

    FROM group_conversation gc
    
    WHERE 
        NULLIF(:keyword, '') IS NULL 
        OR gc.group_name ILIKE CONCAT('%', :keyword, '%')

    ORDER BY
        CASE WHEN :sort = 'created_at' THEN gc.created_at END ASC,
        CASE WHEN :sort = 'group_name' THEN gc.group_name END ASC,
        CASE WHEN :sort = '-created_at' THEN gc.created_at END DESC,
        CASE WHEN :sort = '-group_name' THEN gc.group_name END DESC
    """, nativeQuery = true)
    List<GroupConversationItemListResponse> managementList(String keyword, String sort);



    @Query(value = """
    SELECT ui.username
    FROM user_info ui
    JOIN group_conversation gc ON gc.owner_id = ui.user_id
    WHERE gc.group_conversation_id = :groupId
    """, nativeQuery = true)
    String findOwnerUsername(Long groupId);

}
