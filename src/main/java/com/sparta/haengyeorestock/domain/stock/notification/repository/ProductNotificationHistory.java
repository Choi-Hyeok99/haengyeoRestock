package com.sparta.haengyeorestock.domain.stock.notification.repository;

import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private int replenishmentCount; // 재입고 회차

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품 정복 ( Product 엔티티와 연결 )

    private String status; // 발송 상태
    private Long lastSentUser; // 마지막으로 알람 전송 유저 ID


}
