package com.sparta.haengyeorestock.domain.stock.notification.controller;

import com.sparta.haengyeorestock.domain.stock.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class NotificationController {


    // 재입고 알림 전송 API ( 자동 )
    @PostMapping("/{productId}/notifications/re-stock")
    public ResponseEntity<String> sendRestockNotification(@PathVariable Long productId){
        try{
            NotificationService.sendRestockNotification(productId);
            return ResponseEntity.ok("알림 전송 성공");
        } catch (Exception e){
            return ResponseEntity.status(500).body("알림 전송 실패: " + e.getMessage());
        }
    }
}
