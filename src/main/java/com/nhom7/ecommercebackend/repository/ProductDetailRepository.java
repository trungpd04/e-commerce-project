package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Product;
import com.nhom7.ecommercebackend.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long>, JpaSpecificationExecutor<ProductDetail> {

}
