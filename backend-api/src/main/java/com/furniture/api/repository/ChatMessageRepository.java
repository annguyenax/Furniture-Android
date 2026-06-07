package com.furniture.api.repository;

import com.furniture.api.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    Page<ChatMessage> findByChatIdOrderByCreatedAtDesc(String chatId, Pageable pageable);

    List<ChatMessage> findByChatIdOrderByCreatedAtAsc(String chatId);

    @Query("SELECT DISTINCT c.chatId FROM ChatMessage c WHERE c.senderId = :userId AND c.senderType = 'USER' " +
           "OR c.receiverId = :userId AND c.receiverType = 'USER'")
    List<String> findChatIdsByUserId(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT c.chatId FROM ChatMessage c WHERE c.senderId = :supportId AND c.senderType = 'SHOP' " +
           "OR c.receiverId = :supportId AND c.receiverType = 'SHOP'")
    List<String> findChatIdsBySupportId(@Param("supportId") Integer supportId);

    Long countByChatIdAndIsReadFalseAndReceiverIdAndReceiverType(
        String chatId, Integer receiverId, ChatMessage.SenderType receiverType);

    @Modifying
    @Query("UPDATE ChatMessage c SET c.isRead = true WHERE c.chatId = :chatId AND c.receiverId = :receiverId")
    void markAsRead(@Param("chatId") String chatId, @Param("receiverId") Integer receiverId);
}
