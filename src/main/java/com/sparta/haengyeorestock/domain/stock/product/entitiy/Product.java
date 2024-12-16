package com.sparta.haengyeorestock.domain.stock.product.entitiy;

import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductNotificationHistory;
import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductUserNotification;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // 상품 ID

    private int replenishmentCount; // 재입고 회차
    private int stock; // 재고 수량
    private boolean isOutOfStock; // 품절 여부 ( 재고가 0이면 품절 )

    // ProductNotificationHistory 관계 매핑
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductNotificationHistory> productNotificationHistoryList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductUserNotification> productUserNotifications = new ArrayList<>();


    // 품절 여부를 설정하는 메서드 추가
    public void setIsOutOfStock(boolean isOutOfStock) {
        this.isOutOfStock = isOutOfStock;
    }

    // 재고 감소 메서드
    public void decreaseStock() {
        if (this.stock > 0) {
            this.stock--;
            this.isOutOfStock = this.stock == 0; // 품절 여부 업데이트
        } else {
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }
}
