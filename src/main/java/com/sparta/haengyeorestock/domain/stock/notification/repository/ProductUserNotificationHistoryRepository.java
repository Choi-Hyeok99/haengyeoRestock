package com.sparta.haengyeorestock.domain.stock.notification.repository;

import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotification;
import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotificationHistory;
import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUserNotificationHistoryRepository extends JpaRepository<ProductUserNotificationHistory,Long> {
    List<ProductUserNotification> findAllByProduct(Product product);

}
