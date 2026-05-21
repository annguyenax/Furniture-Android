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
@Table(name = "Sub_Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SubOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_order_id")
    private Integer subOrderId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "shop_id", nullable = false)
    private Integer shopId;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SubOrderStatus status = SubOrderStatus.PENDING;

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
    @JoinColumn(name = "shop_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Shop shop;

    @OneToMany(mappedBy = "subOrder", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "subOrder", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Shipment shipment;

    public enum SubOrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
}
