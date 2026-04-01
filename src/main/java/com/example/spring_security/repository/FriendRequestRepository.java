package com.example.spring_security.repository;

import com.example.spring_security.entities.FriendId;
import com.example.spring_security.entities.FriendRequest;
import com.example.spring_security.entities.FriendRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, FriendRequestId> {

    List<FriendRequest> findBySenderUserId(Long senderId);

    List<FriendRequest> findByReceiverUserId(Long receiverId);

    @Query(value = """
    SELECT fr.*
    FROM friend_request fr
    JOIN user_info u ON u.user_id = fr.sender_id
    WHERE fr.receiver_id = :receiverId
      AND fr.status = 0
      AND fr.is_active = TRUE
      AND (
          NULLIF(:keyword, '') IS NULL
          OR LOWER(CONCAT(u.first_name, ' ', u.last_name))
             LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    """, nativeQuery = true)
    List<FriendRequest> findPendingRequestsReceived(
            @Param("receiverId") Long receiverId,
            @Param("keyword") String keyword
    );



    @Query(value = """
    SELECT fr.*
    FROM friend_request fr
    JOIN user_info u ON u.user_id = fr.receiver_id
    WHERE fr.sender_id = :senderId
      AND fr.status = 0
      AND fr.is_active = TRUE
      AND (
          NULLIF(:keyword, '') IS NULL
          OR LOWER(CONCAT(u.first_name, ' ', u.last_name))
             LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    """, nativeQuery = true)
    List<FriendRequest> findPendingRequestsSent(
            @Param("senderId") Long senderId,
            @Param("keyword") String keyword
    );


    @Query(value = """
        SELECT * 
        FROM friend_request fr
        WHERE (
                (fr.sender_id = :userId1 AND fr.receiver_id = :userId2)
             OR (fr.sender_id = :userId2 AND fr.receiver_id = :userId1)
              )
          AND fr.is_active = TRUE
        ORDER BY fr.sent_at DESC
        """, nativeQuery = true)
    List<FriendRequest> findActiveFriendRequestsBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );


    Optional<FriendRequest> findById(FriendId friendId);

    void deleteById(FriendId id);
}
