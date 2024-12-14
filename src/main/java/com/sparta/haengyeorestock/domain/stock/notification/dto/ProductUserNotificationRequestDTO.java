package com.sparta.haengyeorestock.domain.stock.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUserNotificationRequestDTO {
    private Long productId; // 상품 아디
    private Long userId; // 유저 아디
    private boolean isActive; // 알림 활성화 여부
}
