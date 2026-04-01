package com.example.spring_security.repository;

import com.example.spring_security.entities.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query(value = """
            SELECT * FROM Block B
            WHERE B.blocker_id = :blockerId 
            AND B.blocked_user_id = :blockedUserId 
            AND B.is_active = TRUE
            """, nativeQuery = true)
    Optional<Block> findExistingBlock(Long blockerId, Long blockedUserId);



    @Query(value = """
            SELECT * FROM Block B
            WHERE B.blocker_id = :blockerId
            AND B.blocked_user_id = :blockedUserId
            AND B.blocked_at = :blockedAt
            AND B.is_active = TRUE
            """, nativeQuery = true)
    Optional<Block> findBlock(Long blockerId, Long blockedUserId, LocalDateTime blockedAt);

    @Query(value = """
            SELECT * FROM Block B
            WHERE B.blocker_id = :blockerId 
            AND B.is_active = True
            """, nativeQuery = true)
    List<Block> findAllOf(Long blockerId);

    @Query(value = """
            SELECT EXISTS
            (
                SELECT 1 FROM Block B
                WHERE 
                (
                    (B.blocker_id = :userId1
                    AND 
                    B.blocked_user_id = :userId2)
                    OR
                    (B.blocker_id = :userId2
                    AND 
                    B.blocked_user_id = :userId1)
                )
                AND B.is_active = TRUE
            )
            """, nativeQuery = true)
    boolean existsBlockBetween(Long userId1, Long userId2);
}
