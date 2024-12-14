package com.sparta.haengyeorestock.domain.stock.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUserNotificationResponseDTO {

    private Long productId;
    private int replenishmentCount; // 재입고 회차
    private String status; // 알림 발송 상태
    private String message; // 알림 상태 메시지 ( 성공 / 실패 )
}
