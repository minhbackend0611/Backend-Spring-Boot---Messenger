package com.example.spring_security.repository;


import com.example.spring_security.dto.response.MessageSearchResponse;
import com.example.spring_security.dto.response.UserSearchResponse;
import com.example.spring_security.entities.Enum.Role;
import com.example.spring_security.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByRole(Role role);

    Optional<User> findById(Long id);

    List<User> findAll();

    boolean existsByUsername(@NotBlank(message = "Username must not be blank") @Pattern(regexp = "^[a-zA-Z0-9]{6,30}$", message = "Username must be 6-30 characters long and contain only letters and digits") String username);

    boolean existsByEmail(@NotBlank(message = "Email must not be blank") @Email(message = "Email must be a valid email address") String email);

    Optional<User> findByEmail(String email);

    @Query(value = """
    SELECT 
        u.user_id AS userId,
        u.username AS username,
        u.first_name AS firstName,
        u.last_name AS lastName,
        u.avatar_url AS avatarUrl,

        CASE
            WHEN f.user_id1 IS NOT NULL THEN 'friend'
            WHEN fr.sender_id = :currentUserId THEN 'sent'
            WHEN fr.receiver_id = :currentUserId AND fr.status = 0 THEN 'received'
            ELSE 'none'
        END AS status,

        CASE
            WHEN fr.sender_id = :currentUserId THEN fr.sent_at
            WHEN fr.receiver_id = :currentUserId AND fr.status = 0 THEN fr.sent_at
            ELSE NULL
        END AS sentAt

    FROM user_info u

    LEFT JOIN friend f ON (
        (f.user_id1 = :currentUserId AND f.user_id2 = u.user_id)
        OR 
        (f.user_id2 = :currentUserId AND f.user_id1 = u.user_id)
    )

    LEFT JOIN friend_request fr ON (
        fr.is_active = TRUE
        AND (
            (fr.sender_id = :currentUserId AND fr.receiver_id = u.user_id)
            OR 
            (fr.receiver_id = :currentUserId AND fr.sender_id = u.user_id)
        )
    )

    WHERE u.is_active = TRUE 
      AND u.is_accepted = TRUE 
      AND u.role = 0
      AND (
            u.username ILIKE CONCAT('%', :keyword, '%')
            OR CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', :keyword, '%')
          )
      AND NOT EXISTS (
            SELECT 1 
            FROM Block b
            WHERE (
                (b.blocker_id = :currentUserId AND b.blocked_user_id = u.user_id)
                OR 
                (b.blocker_id = u.user_id AND b.blocked_user_id = :currentUserId)
            )
            AND b.is_active = TRUE
      )

    ORDER BY (u.user_id = :currentUserId) DESC
    """, nativeQuery = true)
    List<UserSearchResponse> searchUserByUsernameOrFullName(
            @Param("currentUserId") Long currentUserId,
            @Param("keyword") String keyword
    );




    @Query(value = """
    SELECT * 
    FROM user_info u
    WHERE
        (
            ( :keyword IS NULL 
              OR :keyword = ''
              OR CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', :keyword, '%')
              OR u.username ILIKE CONCAT('%', :keyword, '%')
              OR u.email ILIKE CONCAT('%', :keyword, '%')
            )
            AND
            ( :username IS NULL 
              OR  :username = ''
              OR u.username ILIKE CONCAT('%', :username, '%')
            )
            AND
            ( :fullName IS NULL 
              OR  :fullName = ''
              OR CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', :fullName, '%')
            )
            AND
            ( :email IS NULL 
              OR  :email = ''
              OR u.email ILIKE CONCAT('%', :email, '%')
            )
        )
        AND ( :isActive IS NULL OR u.is_active = :isActive )
        AND ( :isAccepted IS NULL OR u.is_accepted = :isAccepted )
        AND ( :greaterThan IS NULL OR u.friend_count >= :greaterThan)
        AND ( :smallerThan IS NULL OR u.friend_count <= :smallerThan)
        AND ( :days IS NULL OR u.joined_at  >= NOW() - (:days * INTERVAL '1 day'))
    ORDER BY
        CASE 
            WHEN :sort = 'email' THEN u.email
        END ASC,
        CASE 
            WHEN :sort = '-email' THEN u.email
        END DESC,
        CASE 
            WHEN :sort = 'fullName' THEN LOWER(CONCAT(u.first_name, ' ', u.last_name))
        END ASC,
        CASE 
            WHEN :sort = '-fullName' THEN LOWER(CONCAT(u.first_name, ' ', u.last_name))
        END DESC,
        CASE 
            WHEN :sort = 'joinedAt' THEN u.joined_at
        END ASC,
        CASE 
            WHEN :sort = '-joinedAt' THEN u.joined_at
        END DESC
    """, nativeQuery = true)
    List<User> managementUser(
            @Param("keyword") String keyword,
            @Param("username") String username,
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("isActive") Boolean isActive,
            @Param("isAccepted") Boolean isAccepted,
            @Param("greaterThan") Integer greaterThan,
            @Param("smallerThan") Integer smallerThan,
            @Param("sort") String sort,
            @Param("days") Integer days);

    void deleteById(Long id);


    @Query(value = """
    SELECT 
        pc.private_conversation_id AS id,
        m.private_conversation_message_id AS messageId,
        u.avatar_url AS avatarUrl,
        CONCAT(u.first_name, ' ', u.last_name) AS name,
        m.content AS content,
        m.sent_at AS sentAt,
        m.updated_at AS updatedAt,
        m.type AS messageType,
        CAST(0 AS SMALLINT) AS conversationType,
        '' AS conversationName
    FROM private_conversation_message m
    JOIN private_conversation pc 
        ON pc.private_conversation_id = m.private_conversation_id
    JOIN user_info u
        ON u.user_id = m.sender_id
    
    LEFT JOIN delete_private_conversation dc
        ON dc.private_conversation_id = pc.private_conversation_id
        AND dc.user_id = :userId
    
    WHERE m.private_conversation_id = :conversationId
    
    AND (dc.deleted_at IS NULL OR m.sent_at > dc.deleted_at)
    
    AND NOT EXISTS (
        SELECT 1
        FROM delete_private_conversation_message dm
        WHERE dm.private_conversation_message_id = m.private_conversation_message_id
          AND (dm.is_all = true OR dm.user_id = :userId)
    )
    
    AND (:keyword IS NULL OR m.content ILIKE CONCAT('%', :keyword, '%'))
    
    ORDER BY m.sent_at DESC
""", nativeQuery = true)
    List<MessageSearchResponse> searchPrivateMessages(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );


    @Query(value = """
    SELECT 
        gc.group_conversation_id AS id,
        m.group_conversation_message_id AS messageId,
        u.avatar_url AS avatarUrl,
        CONCAT(u.first_name, ' ', u.last_name) AS name,
        m.content AS content,
        m.sent_at AS sentAt,
        m.updated_at AS updatedAt,
        m.type AS messageType,
        CAST(1 AS SMALLINT) AS conversationType,
        gc.group_name AS conversationName
    FROM group_conversation_message m
    JOIN group_conversation gc
        ON gc.group_conversation_id = m.group_conversation_id
    JOIN user_info u
        ON u.user_id = m.sender_id
    JOIN group_conversation_member gcm
        ON gcm.group_conversation_id = gc.group_conversation_id
        AND gcm.member_id = :userId
    
    LEFT JOIN delete_group_conversation dc
        ON dc.group_conversation_id = gc.group_conversation_id
        AND dc.member_id = :userId
    
    WHERE m.group_conversation_id = :groupId
    
    AND (dc.deleted_at IS NULL OR m.sent_at > dc.deleted_at)
    
    AND NOT EXISTS (
        SELECT 1
        FROM delete_group_conversation_message dm
        WHERE dm.group_conversation_message_id = m.group_conversation_message_id
          AND (dm.is_all = true OR dm.member_id = :userId)
    )
    
    AND (:keyword IS NULL OR m.content ILIKE CONCAT('%', :keyword, '%'))
    
    ORDER BY m.sent_at DESC
""", nativeQuery = true)
    List<MessageSearchResponse> searchGroupMessages(
            @Param("groupId") Long groupId,
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );


    @Query(value = """
    (
        -- ================= PRIVATE =================
        SELECT 
            pc.private_conversation_id AS id,
            m.private_conversation_message_id AS messageId,
            u.avatar_url AS avatarUrl,
            CONCAT(u.first_name, ' ', u.last_name) AS name,
            m.content AS content,
            m.sent_at AS sentAt,
            m.updated_at AS updatedAt,
            m.type AS messageType,
            CAST(0 AS SMALLINT) AS conversationType,
            '' AS conversationName
        FROM private_conversation_message m
        JOIN private_conversation pc
            ON pc.private_conversation_id = m.private_conversation_id
        JOIN user_info u
            ON u.user_id = m.sender_id
    
    
        LEFT JOIN delete_private_conversation dc
            ON dc.private_conversation_id = pc.private_conversation_id
            AND dc.user_id = :userId
    
        WHERE 
            (pc.user1_id = :userId OR pc.user2_id = :userId)
            AND (:keyword IS NULL OR m.content ILIKE CONCAT('%', :keyword, '%'))
    
        
            AND (dc.deleted_at IS NULL OR m.sent_at > dc.deleted_at)
    
         
            AND NOT EXISTS (
                SELECT 1
                FROM delete_private_conversation_message d
                WHERE d.private_conversation_message_id = m.private_conversation_message_id
                  AND (
                      d.is_all = TRUE
                      OR d.user_id = :userId
                  )
            )
    )
    UNION ALL
    (
        -- ================= GROUP =================
        SELECT 
            gcm.group_conversation_id AS id,
            m.group_conversation_message_id AS messageId,
            u.avatar_url AS avatarUrl,
            CONCAT(u.first_name, ' ', u.last_name) AS name,
            m.content AS content,
            m.sent_at AS sentAt,
            m.updated_at AS updatedAt,
            m.type AS messageType,
            CAST(1 AS SMALLINT) AS conversationType,
            g.group_name AS conversationName
        FROM group_conversation_message m
        JOIN group_conversation g ON g.group_conversation_id = m.group_conversation_id
        JOIN group_conversation_member gcm
            ON gcm.group_conversation_id = m.group_conversation_id
            AND gcm.member_id = :userId
        JOIN user_info u
            ON u.user_id = m.sender_id
    
     
        LEFT JOIN delete_group_conversation dc
            ON dc.group_conversation_id = gcm.group_conversation_id
            AND dc.member_id = :userId
    
        WHERE 
            (:keyword IS NULL OR m.content ILIKE CONCAT('%', :keyword, '%'))
    
           
            AND (dc.deleted_at IS NULL OR m.sent_at > dc.deleted_at)
    

            AND NOT EXISTS (
                SELECT 1
                FROM delete_group_conversation_message d
                WHERE d.group_conversation_message_id = m.group_conversation_message_id
                  AND (
                      d.is_all = TRUE
                      OR d.member_id = :userId
                  )
            )
    )
    ORDER BY sentAt DESC
""", nativeQuery = true)
    List<MessageSearchResponse> searchAllMessages(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
}
