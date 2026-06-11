package com.bank.points.service;

import com.bank.points.entity.ProductReview;
import com.bank.points.entity.User;
import com.bank.points.repository.ProductReviewRepository;
import com.bank.points.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ProductReview> getReviewsByProductId(Long productId) {
        List<ProductReview> reviews = productReviewRepository.findByProductIdAndStatus(productId, 1);
        for (ProductReview review : reviews) {
            User user = userRepository.findById(review.getUserId()).orElse(null);
            if (user != null) {
                review.setUsername(user.getUsername());
                review.setRealName(user.getRealName());
            }
        }
        return reviews;
    }

    public List<ProductReview> getReviewsByUserId(Long userId) {
        return productReviewRepository.findByUserId(userId);
    }

    public List<ProductReview> getAllReviewsForAdmin() {
        List<ProductReview> reviews = productReviewRepository.findAll();
        for (ProductReview review : reviews) {
            User user = userRepository.findById(review.getUserId()).orElse(null);
            if (user != null) {
                review.setUsername(user.getUsername());
                review.setRealName(user.getRealName());
            }
        }
        return reviews;
    }

    @Transactional
    public ProductReview addReview(ProductReview review) {
        review.setStatus(1);
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        return productReviewRepository.save(review);
    }

    @Transactional
    public ProductReview updateReviewStatus(Long id, Integer status) {
        ProductReview review = productReviewRepository.findById(id).orElse(null);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        review.setStatus(status);
        review.setUpdateTime(LocalDateTime.now());
        return productReviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        productReviewRepository.deleteById(id);
    }
}
