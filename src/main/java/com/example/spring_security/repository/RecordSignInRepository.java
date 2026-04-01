package com.example.spring_security.repository;


import com.example.spring_security.dto.response.RecordSignInResponse;
import com.example.spring_security.entities.RecordSignIn;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RecordSignInRepository extends JpaRepository<RecordSignIn, Long> {

    @Query(value = """
    SELECT 
        r.record_signin_id AS recordSignInId, 
        r.is_successful AS isSuccessful,
        r.signed_in_at AS signedInAt,
        r.user_id AS userId,
        u.username AS username,
        CONCAT(u.first_name, ' ', u.last_name) AS fullName
    FROM record_signin r
    LEFT JOIN user_info u ON u.user_id = r.user_id
    WHERE (:isSuccessful IS NULL OR r.is_successful = :isSuccessful)
      AND (:userId IS NULL OR u.user_id = :userId)
      AND (:username IS NULL 
            OR u.username ILIKE CONCAT('%', :username, '%'))
    ORDER BY r.signed_in_at DESC
    """, nativeQuery = true)
    List<RecordSignInResponse> findAll(
            @Param("isSuccessful") Boolean isSuccessful,
            @Param("userId") Long userId,
            @Param("username") String username);


    @Query(value = """
    SELECT COUNT(*)
    FROM record_signin r
    JOIN user_info u ON u.user_id = r.user_id
    WHERE (:username IS NULL 
        OR u.username ILIKE CONCAT('%', :username, '%'))
        AND (:userId IS NULL OR :userId = u.user_id)
    """,
            nativeQuery = true)
    Long countAllRecord(@Param("username") String username,
                        @Param("userId") Long userId);



    @Query(value = """
    SELECT COUNT(*)
    FROM record_signin r
    JOIN user_info u ON u.user_id = r.user_id
    WHERE r.is_successful = TRUE
      AND (:username IS NULL 
        OR u.username ILIKE CONCAT('%', :username, '%'))
        AND (:userId IS NULL OR :userId = u.user_id)
    """,
            nativeQuery = true)
    Long countSuccessRecord(@Param("username") String username, @Param("userId") Long userId);


    @Query(value = """
    SELECT COUNT(*)
    FROM record_signin r
    JOIN user_info u ON u.user_id = r.user_id
    WHERE r.is_successful = FALSE
      AND (:username IS NULL 
        OR u.username ILIKE CONCAT('%', :username, '%'))
        AND (:userId IS NULL OR :userId = u.user_id)
    """,
            nativeQuery = true)
    Long countFailRecord(@Param("username") String username, @Param("userId") Long userId);


}
