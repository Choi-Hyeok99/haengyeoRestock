package com.sparta.haengyeorestock.domain.stock.product.repository;

import com.sparta.haengyeorestock.domain.stock.product.entitiy.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
