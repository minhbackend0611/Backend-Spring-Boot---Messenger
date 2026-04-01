package com.example.spring_security.repository;

import com.example.spring_security.dto.response.GroupMemberResponse;
import com.example.spring_security.entities.DeleteGroupConversationId;
import com.example.spring_security.entities.GroupConversationMember;
import com.example.spring_security.entities.GroupConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Optional;

@Repository
public interface GroupConversationMemberRepository
        extends JpaRepository<GroupConversationMember, GroupConversationMemberId> {

    @Query(value = """
            SELECT 
                u.user_id AS userId,
                u.username AS username,
                u.first_name AS firstName,
                u.last_name AS lastName,
                u.avatar_url AS avatarUrl,
                u.is_online AS isOnline,
                gcm.group_role AS groupRole,
                gcm.joined_at AS joinedAt,
                gcm.appointed_at AS appointedAt
                
            FROM group_conversation_member gcm
            JOIN user_info u ON u.user_id = gcm.member_id
            WHERE gcm.group_conversation_id = :groupConversationId
            """, nativeQuery = true)
    List<GroupMemberResponse> findMembersByGroupConversationId(Long groupConversationId);

    @Query(value = """
            SELECT 
                u.user_id AS userId,
                u.username AS username,
                u.first_name AS firstName,
                u.last_name AS lastName,
                u.avatar_url AS avatarUrl,
                u.is_online AS isOnline,
                gcm.group_role AS groupRole,
                gcm.joined_at AS joinedAt,
                gcm.appointed_at AS appointedAt
                
            FROM group_conversation_member gcm
            JOIN user_info u ON u.user_id = gcm.member_id
            WHERE gcm.group_conversation_id = :groupConversationId AND gcm.group_role = 1
            """, nativeQuery = true)
    List<GroupMemberResponse> findAdminsByGroupConversationId(Long groupConversationId);

    @Query(value = """
            SELECT member_id
            FROM group_conversation_member
            WHERE group_conversation_id = :groupConversationId
            """, nativeQuery = true)
    List<Long> findAllMemberIdsOf(Long groupConversationId);

    @Query(value = """
            SELECT *
            FROM group_conversation_member
            WHERE group_conversation_id = :groupConversationId AND member_id = :memberId
            """, nativeQuery = true)
    Optional<GroupConversationMember> findMemberInGroup(@Param("memberId") Long memberId, @Param("groupConversationId") Long groupConversationId);


    void deleteById(GroupConversationMemberId groupConversationMemberId);


    @Query(value = """
            SELECT *
            FROM group_conversation_member gcm
            WHERE group_conversation_id = :groupConversationId
            AND NOT EXISTS (
                SELECT 1
                FROM group_conversation_member gcm1
                WHERE gcm1.group_conversation_id = :groupConversationId 
                AND gcm1.joined_at < gcm.joined_at
            )
            """, nativeQuery = true)
    Optional<GroupConversationMember> findJoinedEarliest(@Param("groupConversationId") Long groupConversationId);


    @Query(value = """
    SELECT COUNT(*) 
    FROM group_conversation_member
    WHERE group_conversation_id = :groupId
    """, nativeQuery = true)
    Long countMembers(Long groupId);

    @Query(value = """
    SELECT COUNT(*) 
    FROM group_conversation_member
    WHERE group_conversation_id = :groupId
      AND group_role = 1
    """, nativeQuery = true)
    Long countAdmins(Long groupId);



}
