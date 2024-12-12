package com.nhom7.ecommercebackend.repository;

import com.nhom7.ecommercebackend.model.Order;
import com.nhom7.ecommercebackend.model.OrderStatus;
import com.nhom7.ecommercebackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {


    @Query("SELECT o FROM Order o " +
            "WHERE (:keyword IS NULL OR REPLACE(LOWER(o.user.fullName), ' ', '') LIKE REPLACE(LOWER(CONCAT('%', :keyword, '%')),' ', '' )) " +
            "AND (:status IS NULL OR TRIM(LOWER(o.status)) = TRIM(LOWER(:status)))")
    Page<Order> findAll(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "WHERE (:user IS NULL OR o.user = :user) " +
            "AND (:status IS NULL OR TRIM(LOWER(o.status)) = TRIM(LOWER(:status)))")
    Page<Order> findAllByStatusAndUser(
            @Param("status") String status,
            @Param("user") User user,
            Pageable pageable);
    List<Order> findAllByUserId(Long userId);

    @Query("SELECT month(o.orderDate) AS month, " +
            "year(o.orderDate) AS year, " +
            "SUM(od.price * od.quantity) AS revenue " +
            "FROM Order o join OrderDetail od on o.id = od.order.id " +
            "where year(o.orderDate) = :year and o.status = 'DELIVERED' " +
            "GROUP BY month(o.orderDate), year(o.orderDate) " +
            "ORDER BY month(o.orderDate) desc ")
    List<Object[]> calculateMonthlyRevenueForYear(@Param("year") int year);

    @Query("SELECT year(o.orderDate) as year, SUM(od.price * od.quantity) AS yearlyrevenue " +
            "FROM Order o join OrderDetail od on o.id = od.order.id " +
            "where year(o.orderDate) = :year and o.status = 'DELIVERED'" +
            "GROUP BY year(o.orderDate)")
    List<Object[]> calculateRevenueForYear(@Param("year") int year);

    @Query("SELECT od.product.id as productid, sum(od.quantity) as quantity, "+
            "SUM(od.price * od.quantity) AS productrevenue "+
            "FROM Order o join OrderDetail od on o.id = od.order.id " +
            "where o.status = 'DELIVERED'" +
            "GROUP BY od.product.id")
    Page<Object[]> calculateProductRevenue(Pageable pageable);
}
