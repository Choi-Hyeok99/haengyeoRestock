package com.sparta.haengyeorestock.domain.stock.notification.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ProductUserNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId; // 알림 이력 ID

    private Long productId; // 상품 ID
    private Long userId; // 유저 ID
    private int replenishmentCount; // 재입고 회차
    private LocalDateTime sentAt; // 알림 전송 시간
}
