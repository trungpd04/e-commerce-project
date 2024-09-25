package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Cart;
import com.nhom7.ecommercebackend.model.CartItem;
import com.nhom7.ecommercebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    boolean existsByProduct(Product product);
    CartItem findByCartAndProduct(Cart cart, Product product);

}
