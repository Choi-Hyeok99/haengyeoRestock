package com.sparta.haengyeorestock.domain.stock.notification.service;

import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotification;
import com.sparta.haengyeorestock.domain.stock.notification.repository.ProductUserNotificationRepository;
import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import com.sparta.haengyeorestock.domain.stock.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DummyDataService {

    private final ProductRepository productRepository;
    private final ProductUserNotificationRepository productUserNotificationRepository;

    @PostConstruct
    public void createDummyData() {

        // 1. 제품 생성
        Product product = new Product();
        product.setStock(5); // 재고 설정
        product.setReplenishmentCount(0); // 재입고 회차 설정
        product.setIsOutOfStock(product.getStock() == 0);
        productRepository.save(product);

        // 2. 유저 알림 설정
        for (int i = 1; i <= 10; i++) {
            ProductUserNotification notification = new ProductUserNotification();
            notification.setProduct(product);
            notification.setUserId((long) i);
            notification.setIsActive(true); // 모든 유저는 알림 활성화
            notification.setCreatedAt(LocalDateTime.now());  // createdAt 설정
            notification.setUpdatedAt(LocalDateTime.now());  // updatedAt 설정

            productUserNotificationRepository.save(notification);
        }
    }
}
