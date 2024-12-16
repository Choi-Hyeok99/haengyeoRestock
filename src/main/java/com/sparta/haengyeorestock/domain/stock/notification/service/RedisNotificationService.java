package com.sparta.haengyeorestock.domain.stock.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisNotificationService {

    private final StringRedisTemplate redisTemplate;
    private final String redisQueueName = "notificationQueue"; // Redis 큐 이름

    // 알림 큐에 유저 알림 추가 (동기적으로 처리)
    public void addNotificationToQueue(Long productId, Long userId) {
        String queueItem = productId + ":" + userId;
        redisTemplate.opsForList().leftPush(redisQueueName, queueItem);  // 큐의 앞에 알림 추가
    }

    // 큐에서 알림 메시지 처리 (동기적으로)
    public void processNotifications(int batchSize) {
        List<String> batchItems = new ArrayList<>();
        String queueItem;

        // 500명씩 묶어서 처리
        while ((queueItem = redisTemplate.opsForList().rightPop(redisQueueName)) != null) {
            batchItems.add(queueItem);

            // 500명이 모이면 처리하고, 배치 비우기
            if (batchItems.size() == batchSize) {
                processBatch(batchItems);
                batchItems.clear();
            }
        }

        // 남은 유저 처리
        if (!batchItems.isEmpty()) {
            processBatch(batchItems);
        }
    }

    // 500명씩 처리
    private void processBatch(List<String> batchItems) {
        for (String item : batchItems) {
            String[] parts = item.split(":");
            Long productId = Long.valueOf(parts[0]);
            Long userId = Long.valueOf(parts[1]);
            sendNotification(userId, productId);
        }
    }

    // 알림 메시지 전송 처리 (동기적으로)
    private void sendNotification(Long userId, Long productId) {
        // 실제 알림 전송 처리 로직
        System.out.println("알림 전송: 사용자 " + userId + " 에게 상품 " + productId + " 재입고 알림 전송");
    }
}
