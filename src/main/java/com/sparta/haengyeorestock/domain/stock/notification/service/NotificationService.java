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
public class NotificationService {

    private final ProductUserNotificationRepository productUserNotificationRepository;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductUserNotificationResponseDTO sendRestockNotification(Long productId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        product.setReplenishmentCount(product.getReplenishmentCount() + 1);
        productRepository.save(product);

        List<ProductUserNotification> notifications = productUserNotificationRepository.findAllByProduct(product);
        List<ProductUserNotificationResponseDTO.UserNotificationStatus> userStatuses = new ArrayList<>();
        int remainingStock = product.getStock(); // 현재 재고
        int completedCount = 0; // 성공적으로 알림을 보낸 유저 수


        // 알림 전송
        for (int i = 0; i < notifications.size(); i++) {
            ProductUserNotification notification = notifications.get(i);

            ProductUserNotificationResponseDTO.UserNotificationStatus userStatus = new ProductUserNotificationResponseDTO.UserNotificationStatus();
            userStatus.setUserId(notification.getUserId());

            // 알림을 활성화한 유저에게만 알림 전송
            if (notification.isActive()) {
                if (remainingStock > 0) {
                    product.decreaseStock();  // 재고 차감
                    sendNotificationToUser(notification.getUserId(), product);
                    userStatus.setNotificationStatus("COMPLETED");
                    saveNotificationHistory(product, notification.getUserId(), ProductNotificationHistory.NotificationStatus.COMPLETED);
                    remainingStock--;  // 재고 차감
                } else if (remainingStock == 0){
                    userStatus.setNotificationStatus("CANCELED_BY_SOLD_OUT");
                    saveNotificationHistory(product, notification.getUserId(), ProductNotificationHistory.NotificationStatus.CANCELED_BY_SOLD_OUT);
                }
            } else {
                userStatus.setNotificationStatus("INACTIVE");
            }

            userStatuses.add(userStatus);
        }
        System.out.println("알림을 성공적으로 보낸 유저 수: " + completedCount);


        // 응답 반환
        ProductUserNotificationResponseDTO response = new ProductUserNotificationResponseDTO();
        response.setProductId(product.getProductId());
        response.setReplenishmentCount(product.getReplenishmentCount());
        response.setStatus("COMPLETED");
        response.setMessage("알림이 성공적으로 전송되었습니다.");
        response.setUserStatuses(userStatuses);

        return response;
    }


    private void saveNotificationHistory(Product product, Long userId, ProductNotificationHistory.NotificationStatus status) {
        ProductNotificationHistory history = new ProductNotificationHistory();
        history.setProduct(product);
        history.setReplenishmentCount(product.getReplenishmentCount());
        history.setStatus(status); // 상태 변경
        history.setLastSentUser(userId); // 마지막 발송 유저 설정
        productNotificationHistoryRepository.save(history); // DB 저장
        System.out.println("Saving Notification History: User " + userId + " Status: " + status);
    }


    private void sendNotificationToUser(Long userId, Product product) {
        System.out.println("유저 ID: " + userId + ", 상품 ID: " + product.getProductId());
    }
}
