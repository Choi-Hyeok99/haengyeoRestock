package com.sparta.haengyeorestock.domain.stock.notification.entitiy;

import jakarta.persistence.*;
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

    private int replenishmentCount; // 재입고 회차
    private LocalDateTime sentAt; // 알림 전송 시간
    private String status; // 알림 상태 (IN_PROGRESS, COMPLETED, CANCELED_BY_SOLD_OUT, CANCELED_BY_ERROR)


    // ProductUserNotification과 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_notification_id")
    private ProductUserNotification productUserNotification; // 유저 알림 설정 정보
}
