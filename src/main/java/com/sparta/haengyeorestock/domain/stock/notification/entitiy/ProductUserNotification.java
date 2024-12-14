package com.sparta.haengyeorestock.domain.stock.notification.entitiy;


import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ProductUserNotification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNotificationId; // 알림 설정 ID

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품 정보

    private Long userId; // 유저 Id ( 따로 관리하지않으니 , userId로만 처리 )
    private boolean isActive; // 알림 활성화 여부

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // ProductUserNotificationHistory와의 관계 설정
    @OneToMany(mappedBy = "productUserNotification", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ProductUserNotificationHistory> productUserNotificationHistoryList = new ArrayList<>();
}
