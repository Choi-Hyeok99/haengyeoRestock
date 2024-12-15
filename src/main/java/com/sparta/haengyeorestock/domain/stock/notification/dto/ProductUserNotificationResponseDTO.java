package com.sparta.haengyeorestock.domain.stock.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductUserNotificationResponseDTO {

    private Long productId;
    private int replenishmentCount; // 재입고 회차
    private String status; // 알림 발송 상태
    private String message; // 알림 상태 메시지 ( 성공 / 실패 )
    private List<UserNotificationStatus> userStatuses; // 유저별 상태

    // 중첩 클래스 정의
    @Data
    @NoArgsConstructor
    public static class UserNotificationStatus {
        private Long userId;
        private String notificationStatus; // 성공/실패
    }
}
