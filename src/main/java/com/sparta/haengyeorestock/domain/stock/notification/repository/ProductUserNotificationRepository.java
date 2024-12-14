package com.sparta.haengyeorestock.domain.stock.notification.repository;

import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotification;
import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification,Long> {
    List<ProductUserNotification> findAllByProduct(Product product);

}
