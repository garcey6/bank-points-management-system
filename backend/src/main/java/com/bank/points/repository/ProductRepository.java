package com.bank.points.repository;

import com.bank.points.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusOrderByCreateTimeDesc(Integer status);

    @Query("SELECT p FROM Product p WHERE p.status = 1 AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%) ORDER BY p.createTime DESC")
    List<Product> searchByKeyword(String keyword);

    List<Product> findByCategoryAndStatus(String category, Integer status);

    @Query("SELECT p FROM Product p WHERE p.status = 1 AND (:category IS NULL OR p.category = :category) AND (:minPoints IS NULL OR p.points >= :minPoints) AND (:maxPoints IS NULL OR p.points <= :maxPoints) ORDER BY p.createTime DESC")
    List<Product> searchProducts(String category, Integer minPoints, Integer maxPoints);
}
