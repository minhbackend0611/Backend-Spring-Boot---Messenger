package com.example.spring_security.repository;

import com.example.spring_security.entities.DeleteGroupConversation;
import com.example.spring_security.entities.DeleteGroupConversationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteGroupConversationRepository extends JpaRepository<DeleteGroupConversation, DeleteGroupConversationId> {
}
