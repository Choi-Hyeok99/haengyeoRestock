package com.sparta.haengyeorestock.domain.stock.notification.repository;

import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistory,Long> {
}
