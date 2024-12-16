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

    // 상태를 업데이트하는 코드 예시
    @Modifying
    @Transactional
    @Query("UPDATE ProductUserNotification p " +
            "SET p.status = :status " +
            "WHERE p.userId = :userId AND p.product.productId = :productId")
    int updateNotificationStatus(Long userId, Long productId, String status);

    boolean existsByProductAndUserId(Product product, long i);
}
