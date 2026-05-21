package com.furniture.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "Return_Requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Integer returnId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "evidence_url", length = 500)
    private String evidenceUrl;

    @Column(name = "evidence_public_id", length = 255)
    private String evidencePublicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", length = 20)
    private EvidenceType evidenceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReturnStatus status = ReturnStatus.PENDING;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", insertable = false, updatable = false)
    @ToString.Exclude
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private User user;

    public enum EvidenceType {
        IMAGE, VIDEO
    }

    public enum ReturnStatus {
        PENDING, APPROVED, REJECTED
    }
}
