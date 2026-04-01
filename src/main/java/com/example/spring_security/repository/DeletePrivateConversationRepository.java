package com.example.spring_security.repository;

import com.example.spring_security.entities.DeletePrivateConversation;
import com.example.spring_security.entities.DeletePrivateConversationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DeletePrivateConversationRepository extends JpaRepository<DeletePrivateConversation, DeletePrivateConversationId> {
    Optional<DeletePrivateConversation> findById(DeletePrivateConversationId id);

    @Query(value = """
            SELECT MAX(dpc.deleted_at)
            FROM delete_private_conversation dpc
            WHERE dpc.user_id = :userId AND dpc.private_conversation_id = :privateConversationId
            """, nativeQuery = true)
    Optional<LocalDateTime> findLastest(Long userId, Long privateConversationId);
}
