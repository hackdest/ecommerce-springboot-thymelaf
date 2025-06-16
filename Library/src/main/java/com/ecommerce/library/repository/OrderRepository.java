package com.ecommerce.library.repository;

import com.ecommerce.library.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year")
    Double getMonthlyRevenue(int month, int year);

    @Query("SELECT SUM(od.quantity *(od.product.costPrice - od.product.salePrice)) FROM OrderDetail od WHERE MONTH(od.order.orderDate) = :month AND YEAR(od.order.orderDate) = :year")
    Double getMonthlyProfit(int month, int year);

    @Query("SELECT od.product.name, SUM(od.quantity) FROM OrderDetail od WHERE MONTH(od.order.orderDate) = :month AND YEAR(od.order.orderDate) = :year GROUP BY od.product.name ORDER BY SUM(od.quantity) DESC LIMIT 1")
    Object[] getBestSellingProductByMonth(int month, int year);

    @Query("SELECT od.product.category.name, SUM(od.quantity) FROM OrderDetail od WHERE MONTH(od.order.orderDate) = :month AND YEAR(od.order.orderDate) = :year GROUP BY od.product.category.name ORDER BY SUM(od.quantity) DESC LIMIT 1")
    Object[] getBestSellingCategoryByMonth(int month, int year);

}
