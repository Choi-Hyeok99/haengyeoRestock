package com.sparta.haengyeorestock.domain.stock.notification.service;

import com.sparta.haengyeorestock.domain.stock.notification.dto.ProductUserNotificationResponseDTO;
import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductNotificationHistory;
import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotification;
import com.sparta.haengyeorestock.domain.stock.notification.repository.ProductNotificationHistoryRepository;
import com.sparta.haengyeorestock.domain.stock.notification.repository.ProductUserNotificationRepository;
import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import com.sparta.haengyeorestock.domain.stock.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")  // 'test' 프로파일을 활성화
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductUserNotificationRepository productUserNotificationRepository;

    @Autowired
    private ProductNotificationHistoryRepository productNotificationHistoryRepository;

    private Long productId;

    @BeforeEach
    public void setup() {
        // 테스트용 데이터 준비
        productId = prepareTestData();
    }
    @AfterEach
    public void cleanup() {
        productUserNotificationRepository.deleteAll();
        productNotificationHistoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    private Long prepareTestData() {
        // 1. 상품 생성
        Product product = new Product();
        product.setStock(500);  // 충분한 재고 설정
        product.setReplenishmentCount(0);
        productRepository.save(product);

        // 2. 500명의 유저 생성 (중복 알림 체크)
        List<ProductUserNotification> notifications = new ArrayList<>();
        for (int i = 1; i <= 500; i++) {
            // 유저의 알림이 이미 존재하는지 확인하여 중복 방지
            if (!productUserNotificationRepository.existsByProductAndUserId(product, (long) i)) {
                ProductUserNotification notification = new ProductUserNotification();
                notification.setProduct(product);
                notification.setUserId((long) i);
                notification.setIsActive(true);  // 알림 활성화
                notifications.add(notification);
            }
        }
        // 중복을 제거한 유저 알림만 삽입
        productUserNotificationRepository.saveAll(notifications);

        return product.getProductId();
    }


    @Test
    @Transactional
    public void testSendRestockNotification() {
        // 재입고 알림 전송
        ProductUserNotificationResponseDTO response = notificationService.sendRestockNotification(productId);

        // 결과 확인 (500명 알림 전송)
        List<ProductNotificationHistory> histories = productNotificationHistoryRepository.findAll();
        assertEquals(500, histories.size(), "500명이 알림을 받아야 합니다.");

        // 알림 상태 확인
        for (ProductNotificationHistory history : histories) {
            // enum의 name() 메서드를 사용하여 알림 상태 비교
            assertEquals("COMPLETED", history.getStatus().name(), "알림 상태는 COMPLETED여야 합니다.");
        }
    }


    @Test
    public void testCodePerformance() {
        // 코드 실행 시간 측정
        long startTime = System.nanoTime();
        notificationService.sendRestockNotification(productId);
        long endTime = System.nanoTime();

        System.out.println("Code Execution Time: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}
