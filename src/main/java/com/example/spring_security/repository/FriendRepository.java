package com.example.spring_security.repository;

import com.example.spring_security.dto.response.UserFriendResponse;
import com.example.spring_security.entities.Friend;
import com.example.spring_security.entities.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId> {


    List<Friend> findByIdUserId1OrIdUserId2(Long userId1, Long userId2);


//    @Query(value = """
//    SELECT u.*
//    FROM friend f
//    JOIN user_info u ON
//    (
//        (f.user_id1 = :userId AND f.user_id2 = u.user_id)
//        OR
//        (f.user_id2 = :userId AND f.user_id1 = u.user_id)
//    )
//    WHERE (:keyword = '' OR CONCAT('%', :keyword, '%') ILIKE CONCAT(u.last_name, ' ', u.first_name))
//    ORDER BY u.is_online DESC
//    """, nativeQuery = true)
//    List<User> findFriendsByUserIdAndKeywordOrderByOnline(
//            @Param("userId") Long userId,
//            @Param("keyword") String keyword);


    @Query(value =  """
         SELECT COUNT(*)
         FROM friend f
         JOIN user_info u ON 
                            (
                                (f.user_id1 = :userId AND f.user_id2 = u.user_id)
                                OR
                                (f.user_id2 = :userId AND f.user_id1 = u.user_id)
                            )
         WHERE (:keyword = '' OR CONCAT(u.last_name, ' ', u.first_name) ILIKE CONCAT('%', :keyword, '%') )
         """, nativeQuery = true)
    Long countFriends(Long userId, String keyword);



    @Query(value = """
    SELECT *
    FROM friend fr
    WHERE (fr.user_id1 = :userId1 AND fr.user_id2 = :userId2)
       OR (fr.user_id1 = :userId2 AND fr.user_id2 = :userId1)
    """, nativeQuery = true)
    Optional<Friend> findExistingFriendBetween(@Param("userId1") Long userId1,
                                               @Param("userId2") Long userId2);



    @Query(value = """
    SELECT 
        u.user_id AS userId,
        u.username AS username,
        u.first_name AS firstName,
        u.last_name AS lastName,
        u.avatar_url AS avatarUrl,
        u.is_online AS isOnline,
        u.address AS address,
        f.made_friend_at AS madeFriendAt
    FROM friend f
    JOIN user_info u ON (
        (f.user_id1 = :userId AND f.user_id2 = u.user_id)
        OR 
        (f.user_id2 = :userId AND f.user_id1 = u.user_id)
    )
    WHERE (:keyword = '' OR CONCAT(u.last_name, ' ', u.first_name) ILIKE CONCAT('%', :keyword, '%'))
    ORDER BY
        CASE WHEN :sort = 'isOnline' THEN u.is_online END DESC,
        CASE WHEN :sort = 'madeFriendAt' THEN f.made_friend_at END ASC,
        CASE WHEN :sort = '-madeFriendAt' THEN f.made_friend_at END DESC,
        CASE WHEN :sort = 'fullName' THEN CONCAT(u.first_name, ' ', u.last_name) END ASC,
        CASE WHEN :sort = '-fullName' THEN CONCAT(u.first_name, ' ', u.last_name) END DESC
""", nativeQuery = true)
    List<UserFriendResponse> findAllFriendsByUserIdAndKeywordOrderBy(@Param("userId") Long userId,
                                                                     @Param("keyword") String keyword,
                                                                     @Param("sort") String sort);


    @Query(value = """
    SELECT 
        u.user_id AS userId,
        u.username AS username,
        u.first_name AS firstName,
        u.last_name AS lastName,
        u.avatar_url AS avatarUrl,
        u.is_online AS isOnline,
        u.address AS address,
        f.made_friend_at AS madeFriendAt
    FROM friend f
    JOIN user_info u ON (
        (f.user_id1 = :userId AND f.user_id2 = u.user_id)
        OR 
        (f.user_id2 = :userId AND f.user_id1 = u.user_id)
    )
    WHERE (:keyword = '' OR CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', :keyword, '%'))
    AND NOT EXISTS (
                        SELECT 1
                        FROM group_conversation_member gc
                        WHERE gc.member_id = u.user_id AND gc.group_conversation_id = :groupConversationId
                   )
    ORDER BY u.is_online DESC
""", nativeQuery = true)
    List<UserFriendResponse> findFriendsToAddGroup(@Param("userId") Long userId,
                                                  @Param("groupConversationId") Long groupConversationId,
                                                  @Param("keyword") String keyword);

}
