package com.sparta.haengyeorestock.domain.stock.notification.entitiy;

import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ProductNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId; // 알림 히스토리 ID

    private int replenishmentCount; // 재입고 회차

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품 정복 ( Product 엔티티와 연결 )

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    private Long lastSentUser; // 마지막으로 알람 전송 유저 ID

    public enum NotificationStatus {
        IN_PROGRESS,
        COMPLETED,
        CANCELED_BY_SOLD_OUT,
        CANCELED_BY_ERROR
    }
}
