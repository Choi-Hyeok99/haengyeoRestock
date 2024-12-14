package com.sparta.haengyeorestock.domain.stock.notification.controller;

import com.sparta.haengyeorestock.domain.stock.notification.dto.ProductUserNotificationResponseDTO;
import com.sparta.haengyeorestock.domain.stock.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class NotificationController {

    private final NotificationService notificationService;

    // 자동 재입고 알림 전송 API
    @PostMapping("/{productId}/notifications/re-stock")
    public ResponseEntity<ProductUserNotificationResponseDTO> sendRestockNotification(@PathVariable Long productId) {
        try {
            // 서비스 호출하여 알림 전송 처리
            ProductUserNotificationResponseDTO response = notificationService.sendRestockNotification(productId);
            // 알림 전송 후, DTO와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 처리 시, 오류 메시지와 함께 INTERNAL_SERVER_ERROR 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
