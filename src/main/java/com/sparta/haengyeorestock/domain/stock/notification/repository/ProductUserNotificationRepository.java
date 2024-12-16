package com.sparta.haengyeorestock.domain.stock.notification.repository;

import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotification;
import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification,Long> {
    List<ProductUserNotification> findAllByProduct(Product product);

    // 모든 유저의 상태를 CANCELED_BY_SOLD_OUT으로 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE ProductUserNotification p " +
            "SET p.status = 'CANCELED_BY_SOLD_OUT' " +
            "WHERE p.product.productId = :productId")
    int updateStatusToSoldOut(Long productId);
}
