package com.furniture.api.repository;

import com.furniture.api.model.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {

    List<AiChatMessage> findTop20ByUserIdAndConversationIdOrderByCreatedAtDesc(Integer userId, String conversationId);

    List<AiChatMessage> findByUserIdAndConversationIdOrderByCreatedAtAsc(Integer userId, String conversationId);

    Optional<AiChatMessage> findTopByUserIdOrderByCreatedAtDesc(Integer userId);

    void deleteByUserIdAndConversationId(Integer userId, String conversationId);
}
