package com.sparta.haengyeorestock.domain.stock.product.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // 상품 ID

    private int replenishmentCount; // 재입고 회차
    private int stock; // 재고 수량
    private boolean isOutOfStock; // 품절 여부 ( 재고가 0이면 품절 )


}
