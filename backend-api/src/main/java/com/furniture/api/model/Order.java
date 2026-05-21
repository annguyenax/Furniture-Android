package com.furniture.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "shipping_address_id", nullable = false)
    private Integer shippingAddressId;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "shipping_fee", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Address shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<SubOrder> subOrders = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    public enum PaymentMethod {
        COD, MOMO, VNPAY, BANK_TRANSFER
    }

    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }

    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED, CANCELLED
    }
}
