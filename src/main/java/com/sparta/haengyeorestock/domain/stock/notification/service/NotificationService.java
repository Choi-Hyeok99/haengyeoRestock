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
    private final RedisNotificationService redisNotificationService; // RedisNotificationService 의존성 추가

    @Transactional
    public ProductUserNotificationResponseDTO sendRestockNotification(Long productId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 재입고 카운트 증가
        product.setReplenishmentCount(product.getReplenishmentCount() + 1);
        productRepository.save(product);

        List<ProductUserNotification> notifications = productUserNotificationRepository.findAllByProduct(product);
        List<ProductUserNotificationResponseDTO.UserNotificationStatus> userStatuses = new ArrayList<>();
        int remainingStock = product.getStock(); // 현재 재고
        int batchSize = 500;  // 배치 크기 설정 (500명씩 처리)
        int completedCount = 0; // 성공적으로 알림을 보낸 유저 수

        // 알림 전송 (500명씩 묶어서 처리)
        for (int i = 0; i < notifications.size(); i++) {
            ProductUserNotification notification = notifications.get(i);

            ProductUserNotificationResponseDTO.UserNotificationStatus userStatus = new ProductUserNotificationResponseDTO.UserNotificationStatus();
            userStatus.setUserId(notification.getUserId());

            // 알림을 활성화한 유저에게만 알림 전송
            if (notification.isActive()) {
                if (remainingStock > 0) {
                    // 재고가 남아있으면 알림 전송
                    product.decreaseStock();  // 재고 차감
                    sendNotificationToUser(notification.getUserId(), product);
                    userStatus.setNotificationStatus("COMPLETED");
                    // 히스토리 저장
                    saveNotificationHistory(product, notification.getUserId(), ProductNotificationHistory.NotificationStatus.COMPLETED);
                    remainingStock--;  // 재고 차감
                    completedCount++;
                } else {
                    // 재고가 없으면 상태를 CANCELED_BY_SOLD_OUT으로 설정
                    userStatus.setNotificationStatus("CANCELED_BY_SOLD_OUT");
                    // 히스토리 저장
                    saveNotificationHistory(product, notification.getUserId(), ProductNotificationHistory.NotificationStatus.CANCELED_BY_SOLD_OUT);
                    // 알림 중단 (재고가 부족하면 더 이상 알림을 전송하지 않음)
                    break;
                }
            } else {
                userStatus.setNotificationStatus("INACTIVE");
            }

            userStatuses.add(userStatus);

            // 500명씩 처리 완료 후, 큐에 알림 추가 (배치 처리)
            if ((i + 1) % batchSize == 0 || i == notifications.size() - 1) {
                redisNotificationService.addNotificationToQueue(productId, notification.getUserId());
            }
        }

        // 응답 반환
        ProductUserNotificationResponseDTO response = new ProductUserNotificationResponseDTO();
        response.setProductId(product.getProductId());
        response.setReplenishmentCount(product.getReplenishmentCount());
        response.setStatus("COMPLETED");
        response.setMessage("알림이 성공적으로 전송되었습니다.");
        response.setUserStatuses(userStatuses);

        System.out.println("알림을 성공적으로 보낸 유저 수: " + completedCount);
        return response;
    }

    // 알림 전송 상태를 저장하는 메서드
    private void saveNotificationHistory(Product product, Long userId, ProductNotificationHistory.NotificationStatus status) {
        ProductNotificationHistory history = new ProductNotificationHistory();
        history.setProduct(product);
        history.setReplenishmentCount(product.getReplenishmentCount());
        history.setStatus(status); // 상태 변경
        history.setLastSentUser(userId); // 마지막 발송 유저 설정
        productNotificationHistoryRepository.save(history); // DB 저장
    }


    private void sendNotificationToUser(Long userId, Product product) {
        // 실제 알림 전송 로직을 구현합니다. 예시로는 콘솔에 출력하는 코드입니다.
        System.out.println("알림 전송: 사용자 " + userId + " 에게 상품 " + product.getProductId() + " 재입고 알림 전송");
    }
}
