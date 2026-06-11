package com.bank.points.service;

import com.bank.points.entity.Product;
import com.bank.points.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PointsService pointsService;

    public List<Product> getAllProducts() {
        return productRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    public List<Product> filterProducts(String category, Integer minPoints, Integer maxPoints) {
        return productRepository.searchProducts(category, minPoints, maxPoints);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndStatus(category, 1);
    }

    @Transactional
    public Product createProduct(Product product) {
        if (product.getDiscount() == null) {
            product.setDiscount(1.0);
        }
        product.setStatus(1);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStock(product.getStock() - quantity);
        product.setUpdateTime(LocalDateTime.now());
        productRepository.save(product);
    }

    public Integer getDiscountedPoints(Long productId, Integer memberLevel) {
        Product product = getProductById(productId);
        if (product == null) {
            return 0;
        }
        Double discountRate = pointsService.getMemberDiscountRate(memberLevel);
        return (int) Math.ceil(product.getPoints() * product.getDiscount() * discountRate);
    }
}
