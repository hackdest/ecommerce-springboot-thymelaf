package com.ecommerce.library.repository;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetail;
import com.ecommerce.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("select o from Order o where o.customer.id = ?1")
    List<Order> findAllByCustomerId(Long id);

    boolean existsByOrderAndProduct(Order order, Product product);
}
