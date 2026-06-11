package com.bank.points.service;

import com.bank.points.entity.Cart;
import com.bank.points.entity.Product;
import com.bank.points.repository.CartRepository;
import com.bank.points.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Map<String, Object>> getUserCartWithProducts(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", cart.getId());
                item.put("userId", cart.getUserId());
                item.put("productId", product.getId());
                item.put("productName", product.getName());
                item.put("productImage", product.getImageUrl());
                item.put("productPoints", product.getPoints());
                item.put("productStock", product.getStock());
                item.put("quantity", cart.getQuantity());
                item.put("totalPoints", product.getPoints() * cart.getQuantity());
                result.add(item);
            }
        }
        return result;
    }

    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }

        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            existingCart.setUpdateTime(LocalDateTime.now());
            return cartRepository.save(existingCart);
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCartQuantity(Long cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new RuntimeException("购物车项不存在");
        }
        if (quantity <= 0) {
            cartRepository.delete(cart);
            return null;
        }
        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(carts);
    }
}
