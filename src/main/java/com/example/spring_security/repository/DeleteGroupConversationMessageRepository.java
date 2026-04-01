package com.example.spring_security.repository;

import com.example.spring_security.entities.DeleteGroupConversationMessage;
import com.example.spring_security.entities.DeleteGroupConversationMessageId;
import com.example.spring_security.entities.DeletePrivateConversationMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeleteGroupConversationMessageRepository
        extends JpaRepository<DeleteGroupConversationMessage, DeleteGroupConversationMessageId> {

    Optional<DeleteGroupConversationMessage> findById(DeletePrivateConversationMessageId id);

}
