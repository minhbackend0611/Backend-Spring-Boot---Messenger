package com.example.spring_security.repository;

import com.example.spring_security.entities.DeletePrivateConversationId;
import com.example.spring_security.entities.DeletePrivateConversationMessage;
import com.example.spring_security.entities.DeletePrivateConversationMessageId;
import com.example.spring_security.entities.PrivateConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DeletePrivateConversationMessageRepository
        extends JpaRepository<DeletePrivateConversationMessage, DeletePrivateConversationMessageId> {

    Optional<DeletePrivateConversationMessage> findById(DeletePrivateConversationMessageId deletePrivateConversationMessageId);

}
