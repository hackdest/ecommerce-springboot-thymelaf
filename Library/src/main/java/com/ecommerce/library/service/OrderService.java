package com.ecommerce.library.service;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;

import java.util.Date;
import java.util.List;


public interface OrderService {
//    Order save(ShoppingCart shoppingCart);
    Order save(ShoppingCart shoppingCart, String paymentMethod);

    List<Order> findAll(String username);

    List<Order> findALlOrders();

    Order acceptOrder(Long id);

    void cancelOrder(Long id);
     void  updatePaymentSucess(Order order);




}
