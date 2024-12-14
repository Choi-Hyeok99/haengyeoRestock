package com.sparta.haengyeorestock.domain.stock.product.entitiy;

import com.sparta.haengyeorestock.domain.stock.notification.entitiy.ProductNotificationHistory;
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

}
