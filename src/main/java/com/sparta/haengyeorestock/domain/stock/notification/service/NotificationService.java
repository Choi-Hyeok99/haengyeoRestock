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
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 재입고 회차 1 증가
        product.setReplenishmentCount(product.getReplenishmentCount() + 1);
        productRepository.save(product);

        // 알림 설정한 유저 목록 가져오기
        List<ProductUserNotification> notifications = productUserNotificationRepository.findAllByProduct(product);

        int remainingStock = product.getStock();

        // 일반 for문 사용
        for (int i = 0; i < notifications.size(); i++) {
            ProductUserNotification notification = notifications.get(i);

            // 재고가 없으면 알림을 보내지 않음
            if (remainingStock <= 0) {
                saveNotificationHistory(product, notification.getUserId(), "CANCELED_BY_SOLD_OUT");
                break; // 더 이상 알림을 보낼 필요가 없으므로 중단
            }

            if (!notification.isActive()) continue;  // 알림 설정이 비활성화된 유저는 넘김

            Long userId = notification.getUserId();
            sendNotificationToUser(userId, product);

            // 알림 상태 기록
            saveNotificationHistory(product, userId, "COMPLETED");

            remainingStock--;  // 재고 감소
        }

        // 응답 DTO 설정
        ProductUserNotificationResponseDTO response = new ProductUserNotificationResponseDTO();
        response.setProductId(product.getProductId());
        response.setReplenishmentCount(product.getReplenishmentCount());
        response.setStatus("COMPLETED");
        response.setMessage("알림이 성공적으로 전송되었습니다.");

        return response;
    }
    // 히스토리 남기는 메소드
    private void saveNotificationHistory(Product product, Long userId, String status) {
        ProductNotificationHistory history = new ProductNotificationHistory();
        history.setProduct(product);
        history.setReplenishmentCount(product.getReplenishmentCount());
        history.setStatus(status);
        history.setLastSentUser(userId);
        productNotificationHistoryRepository.save(history);
    }

    private void sendNotificationToUser(Long userId, Product product) {
        // 유저에게 알림을 전송하는 메서드
        System.out.println("유저 ID: " + userId + ", 상품 ID: " + product.getProductId());
    }
}