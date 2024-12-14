package com.sparta.haengyeorestock.domain.stock.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProductNotificationHistoryDTO {

    private Long userId;
    private Long productId;
    private int replenishmentCount; // 재입고 회차
    private LocalDateTime sentAt; // 알림 발송 시간

}
