package com.example.spring_security.repository;

import com.example.spring_security.dto.response.UserRecordOnlineResponse;
import com.example.spring_security.entities.RecordOnlineUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RecordOnlineUserRepository extends JpaRepository<RecordOnlineUser, String> {

    Optional<RecordOnlineUser> findById(String sessionId);

    @Query(value = """
        SELECT
            u.user_id AS userId,
            u.username AS username,
            u.first_name AS firstName,
            u.last_name AS lastName,
            u.avatar_url AS avatarUrl,
            u.is_online AS isOnline,
            u.joined_at AS joinedAt,
            COUNT(r.session_id) AS activityCount,
            MAX(r.online_at) AS lastOnlineAt,
            SUM(
                    (EXTRACT(EPOCH FROM (COALESCE(r.offline_at, NOW()) - r.online_at)))::bigint
                )::bigint AS totalOnlineSeconds
        FROM user_info u
        LEFT JOIN record_online_user r ON r.user_id = u.user_id
        WHERE (:keyword IS NULL OR CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', :keyword, '%'))
        GROUP BY u.user_id, u.username, u.first_name, u.last_name, u.avatar_url, u.is_online
        HAVING 
            (:greaterThan IS NULL OR COUNT(r.session_id) >= :greaterThan)
            AND 
            (:smallerThan IS NULL OR COUNT(r.session_id) <= :smallerThan)
        ORDER BY
            CASE WHEN :sort = 'fullName' THEN CONCAT(u.first_name, ' ', u.last_name) END ASC,
            CASE WHEN :sort = '-fullName' THEN CONCAT(u.first_name, ' ', u.last_name) END DESC,
            CASE WHEN :sort = 'createdAt' THEN u.joined_at END ASC,
            CASE WHEN :sort = '-createdAt' THEN u.joined_at END DESC;
        """, nativeQuery = true)
    List<UserRecordOnlineResponse> managementRecordList(
            String keyword,
            String sort,
            Long greaterThan,
            Long smallerThan
    );

}
