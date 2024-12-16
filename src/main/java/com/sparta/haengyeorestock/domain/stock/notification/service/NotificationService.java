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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final ProductUserNotificationRepository productUserNotificationRepository;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;
    private final ProductRepository productRepository;

    public ProductUserNotificationResponseDTO sendRestockNotification(Long productId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        product.setReplenishmentCount(product.getReplenishmentCount() + 1);
        productRepository.save(product);

        List<ProductUserNotification> notifications = productUserNotificationRepository.findAllByProduct(product);
        List<ProductUserNotificationResponseDTO.UserNotificationStatus> userStatuses = new ArrayList<>();
        int remainingStock = product.getStock(); // 현재 재고

        for (ProductUserNotification notification : notifications) {
            ProductUserNotificationResponseDTO.UserNotificationStatus userStatus = new ProductUserNotificationResponseDTO.UserNotificationStatus();
            userStatus.setUserId(notification.getUserId());

            if (remainingStock > 0 && notification.isActive()) { // 재고가 남아있고 알림 활성화된 유저
                product.decreaseStock(); // 재고 감소 메서드
                sendNotificationToUser(notification.getUserId(), product);
                userStatus.setNotificationStatus("COMPLETED");
                saveNotificationHistory(product, notification.getUserId(), "COMPLETED");
                remainingStock--;  // 재고 차감
            } else if (remainingStock <= 0) { // 재고가 없을 때
                userStatus.setNotificationStatus("CANCELED_BY_SOLD_OUT");
                saveNotificationHistory(product, notification.getUserId(), "CANCELED_BY_SOLD_OUT");
            } else { // 알림 비활성화된 유저
                userStatus.setNotificationStatus("INACTIVE");
            }

            userStatuses.add(userStatus);
        }

        ProductUserNotificationResponseDTO response = new ProductUserNotificationResponseDTO();
        response.setProductId(product.getProductId());
        response.setReplenishmentCount(product.getReplenishmentCount());
        response.setStatus("COMPLETED");
        response.setMessage("알림이 성공적으로 전송되었습니다.");
        response.setUserStatuses(userStatuses);

        return response;
    }

    private void saveNotificationHistory(Product product, Long userId, String status) {
        ProductNotificationHistory history = new ProductNotificationHistory();
        history.setProduct(product);
        history.setReplenishmentCount(product.getReplenishmentCount());
        history.setStatus(status);
        history.setLastSentUser(userId);
        productNotificationHistoryRepository.save(history);
    }

    private void sendNotificationToUser(Long userId, Product product) {
        System.out.println("유저 ID: " + userId + ", 상품 ID: " + product.getProductId());
    }
}
