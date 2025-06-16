package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.*;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.repository.OrderDetailRepository;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository detailRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartService cartService;
    private final ProductRepository productRepository;


    @Override
    @Transactional
    public Order save(ShoppingCart shoppingCart, String paymentMethod) {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setCustomer(shoppingCart.getCustomer());
        order.setTax(10);
        order.setTotalPrice(shoppingCart.getTotalPrice());
        order.setAccept(false);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus("Đang xử lý");
        order.setQuantity(shoppingCart.getTotalItems());


        // Lưu order trước
        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartItem item : shoppingCart.getCartItems()) {
            updateQty(item);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder); // Sử dụng order đã lưu
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity()); // Thiết lập giá trị quantity


            // Kiểm tra xem OrderDetail đã tồn tại hay chưa
            boolean exists = detailRepository.existsByOrderAndProduct(savedOrder, item.getProduct());
            if (!exists) {
                detailRepository.save(orderDetail);
                orderDetailList.add(orderDetail);
            }
        }
        savedOrder.setOrderDetailList(orderDetailList);
        cartService.deleteCartById(shoppingCart.getId());
        return savedOrder;
    }

    private void updateQty(CartItem cartItem) {
        int qty = cartItem.getQuantity();
        Long productID = cartItem.getProduct().getId();
        int productqty = productRepository.getProductQty(productID);
        productRepository.updateQty(productqty-qty,productID);
    }

    @Override
    public List<Order> findAll(String username) {
        Customer customer = customerRepository.findByUsername(username);
        List<Order> orders = customer.getOrders();
        return orders;
    }

    @Override
    public List<Order> findALlOrders() {
        return orderRepository.findAll();
    }


    @Override
    public Order acceptOrder(Long id) {
        Order order = orderRepository.getById(id);
        order.setAccept(true);

        // mới thêm dòng này
        order.setOrderStatus("thành công");

        order.setDeliveryDate(new Date());
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void  updatePaymentSucess(Order order){
        orderRepository.save(order);
    }


}
