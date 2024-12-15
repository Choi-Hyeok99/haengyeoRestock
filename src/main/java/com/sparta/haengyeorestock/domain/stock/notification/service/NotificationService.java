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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ProductUserNotificationRepository productUserNotificationRepository;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;
    private final ProductRepository productRepository;

    // 자동 재입고 알림 전송
    public ProductUserNotificationResponseDTO sendRestockNotification(Long productId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        product.setReplenishmentCount(product.getReplenishmentCount() + 1);
        productRepository.save(product);

        List<ProductUserNotification> notifications = productUserNotificationRepository.findAllByProduct(product);
        List<ProductUserNotificationResponseDTO.UserNotificationStatus> userStatuses = new ArrayList<>();
        int remainingStock = product.getStock();

        for (ProductUserNotification notification : notifications) {
            ProductUserNotificationResponseDTO.UserNotificationStatus userStatus = new ProductUserNotificationResponseDTO.UserNotificationStatus();
            userStatus.setUserId(notification.getUserId());

            if (remainingStock <= 0) {
                userStatus.setNotificationStatus("CANCELED_BY_SOLD_OUT");
                saveNotificationHistory(product, notification.getUserId(), "CANCELED_BY_SOLD_OUT");
            } else if (notification.isActive()) {
                sendNotificationToUser(notification.getUserId(), product);
                userStatus.setNotificationStatus("COMPLETED");
                saveNotificationHistory(product, notification.getUserId(), "COMPLETED");
                remainingStock--;
            } else {
                userStatus.setNotificationStatus("INACTIVE");
            }

            userStatuses.add(userStatus);
        }

        ProductUserNotificationResponseDTO response = new ProductUserNotificationResponseDTO();
        response.setProductId(product.getProductId());
        response.setReplenishmentCount(product.getReplenishmentCount());
        response.setStatus("COMPLETED");
        response.setMessage("알림이 성공적으로 전송되었습니다.");
        response.setUserStatuses(userStatuses); // 유저별 상태 추가

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