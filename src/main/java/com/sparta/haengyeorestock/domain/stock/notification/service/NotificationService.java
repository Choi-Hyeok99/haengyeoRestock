package com.sparta.haengyeorestock.domain.stock.notification.service;

import com.sparta.haengyeorestock.domain.stock.notification.dto.ProductUserNotificationResponseDTO;
import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductNotificationHistory;
import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotification;
import com.sparta.haengyeorestock.domain.stock.notification.repository.ProductNotificationHistoryRepository;
import com.sparta.haengyeorestock.domain.stock.notification.repository.ProductUserNotificationRepository;
import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import com.sparta.haengyeorestock.domain.stock.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ProductUserNotificationRepository productUserNotificationRepository;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;
    private final ProductRepository productRepository;

    // 자동 재입고 알림 전송
    public ProductUserNotificationResponseDTO sendRestockNotification(Long productId) {
        // 상품 확인
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 재입고 회차 1 증가
        product.setReplenishmentCount(product.getReplenishmentCount() + 1);
        productRepository.save(product);

        // 알림 설정한 유저 목록 가져오기
        List<ProductUserNotification> notifications = productUserNotificationRepository.findAllByProduct(product);

        // 알림 순차적 전송
        for (int i = 0; i < notifications.size(); i++) {
            ProductUserNotification notification = notifications.get(i);
            if (!notification.isActive()) continue;

            Long userId = notification.getUserId();
            sendNotificationToUser(userId, product);

            // 알림 상태 기록
            ProductNotificationHistory history = new ProductNotificationHistory();
            history.setProduct(product);
            history.setReplenishmentCount(product.getReplenishmentCount());
            history.setStatus("COMPLETED");
            history.setLastSentUser(userId);
            productNotificationHistoryRepository.save(history);
        }

        // DTO 반환
        ProductUserNotificationResponseDTO response = new ProductUserNotificationResponseDTO();
        response.setProductId(product.getProductId());
        response.setReplenishmentCount(product.getReplenishmentCount());
        response.setStatus("COMPLETED");
        response.setMessage("알림이 성공적으로 전송되었습니다.");

        return response;
    }

    private void sendNotificationToUser(Long userId, Product product) {
        // 유저에게 알림을 전송하는 메서드
        System.out.println("유저 ID: " + userId + ", 상품 ID: " + product.getProductId());
    }
}
