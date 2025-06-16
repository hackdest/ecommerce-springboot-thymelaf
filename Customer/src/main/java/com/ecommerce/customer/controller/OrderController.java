package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.*;
import com.ecommerce.library.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.commons.codec.digest.HmacUtils;

import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final CustomerService customerService;
    private final OrderService orderService;

    private final ShoppingCartService cartService;



    @GetMapping("/check-out")
    public String checkOut(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            CustomerDto customer = customerService.getCustomer(principal.getName());
            if (customer.getAddress() == null  || customer.getPhoneNumber() == null) {
                model.addAttribute("information", "You need update your information before check out");
                model.addAttribute("customer", customer);
                model.addAttribute("title", "Profile");
                model.addAttribute("page", "Profile");
                return "customer-information";
            } else {
                ShoppingCart cart = customerService.findByUsername(principal.getName()).getCart();
                model.addAttribute("customer", customer);
                model.addAttribute("title", "Check-Out");
                model.addAttribute("page", "Check-Out");
                model.addAttribute("shoppingCart", cart);
                model.addAttribute("grandTotal", cart.getTotalItems());
                return "checkout";

            }
        }
    }

    @GetMapping("/orders")
    public String getOrders(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            List<Order> orderList = customer.getOrders();
            model.addAttribute("orders", orderList);
            model.addAttribute("title", "Order");
            model.addAttribute("page", "Order");
            return "order";
        }
    }
@PostMapping("/cancel-order")
public String cancelOrder(@RequestParam Long id, RedirectAttributes attributes) {
    try {
        orderService.cancelOrder(id);
        attributes.addFlashAttribute("success", "Cancel order successfully!");
    } catch (Exception e) {
        // Handle the exception, e.g., log it, send an email, etc.
        attributes.addFlashAttribute("error", "An error occurred while canceling the order.");
    }
    return "redirect:/orders";
}

    @RequestMapping(value = "/add-order", method = {RequestMethod.POST})
    public String createOrder(Principal principal,
                              Model model,
                              HttpSession session,
                              @RequestParam(value = "paymentMethod", required = false) String paymentMethod) {
        if (principal == null) {
            return "redirect:/login";
        }
        else
        {
            Customer customer = customerService.findByUsername(principal.getName());
            ShoppingCart cart = customer.getCart();
            System.out.println(cart.getTotalPrice()+ "chưa bấm momo checkout");
            Order order = orderService.save(cart, paymentMethod);

            if ("momo".equals(paymentMethod)) {
                System.out.println(cart.getTotalPrice()+ "momo checkowwwwwwut");

                LocalDateTime ngayGio = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String ngayGioFormatted = ngayGio.format(formatter);
                String orderId = "hoaDon" + order.getId() + "H" + ngayGioFormatted;

                String total = convertDoubleToString(order.getTotalPrice()+ order.getTax()); // Chuyển đổi số tiền tại đây
//                String total = Double.toString(order.getTotalPrice());
                System.out.println(total);
                String paymentRedirect = createPayment(total, orderId);
                if (paymentRedirect.startsWith("redirect:")) {
                    return paymentRedirect;
                }
            }
            session.removeAttribute("totalItems");
            model.addAttribute("order", order);
            model.addAttribute("title", "Order Detail");
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Add order successfully");
            return "order-detail";
        }
    }

    @GetMapping("/callback")
    public String handleMoMoCallback(
            @RequestParam("orderId") String orderId,
            @RequestParam("transId") String transId,
            @RequestParam("resultCode") String resultCode,
            @RequestParam("message") String message,
            @RequestParam("signature") String signature) {
        if ("0".equals(resultCode)) {
            int viTriH = orderId.indexOf('H');
            System.out.println(viTriH);
            // cập nhập đơn hàng nếu khách thanh toán theo hóa đơn
            if (orderId.contains("hoaDon")) {
                String maHD = orderId.substring(6, viTriH);
                Order  order = orderService.acceptOrder(Long.parseLong(maHD));
                order.setPaymentMethod("Momo(thành công)");
                orderService.updatePaymentSucess(order);
                return "redirect:/";
            }

        } else {
            // Xử lý trường hợp thanh toán không thành công
            return "redirect:/";
        }
        return "redirect:/";
    }

    public String createPayment(String total,String id) {
        String partnerCode = "MOMO";
        String accessKey = "F8BBA842ECF85";
        String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
        String requestId = partnerCode + new Date().getTime();
        String orderId = id;
        String orderInfo = "pay with MoMo";
        String redirectUrl = "http://localhost:8020/shop/callback";
        String ipnUrl = "http://localhost:8020";
        String amount = total;
        String requestType = "captureWallet";
        String extraData = "";
        String rawSignature = "accessKey=" + accessKey + "&amount=" + amount + "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode + "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId + "&requestType=" + requestType;
        String signature = HmacUtils.hmacSha256Hex(secretKey, rawSignature);
        String requestBody = String.format(
                "{\"partnerCode\":\"%s\",\"accessKey\":\"%s\",\"requestId\":\"%s\",\"amount\":\"%s\",\"orderId\":\"%s\"," +
                        "\"orderInfo\":\"%s\",\"redirectUrl\":\"%s\",\"ipnUrl\":\"%s\",\"extraData\":\"%s\",\"requestType\":\"%s\",\"signature\":\"%s\",\"lang\":\"en\"}",
                partnerCode, accessKey, requestId, amount, orderId, orderInfo, redirectUrl, ipnUrl, extraData, requestType, signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        String endpointUrl = "https://test-payment.momo.vn/v2/gateway/api/create";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpointUrl, httpEntity, String.class);
        String responseBody = responseEntity.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String payUrl = jsonNode.get("payUrl").asText();

            System.out.println("PayUrl: " + payUrl);
            return "redirect:"+payUrl;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    public static String convertDoubleToString(double value) {
        // Định dạng số với tối đa 2 chữ số thập phân
        DecimalFormat df = new DecimalFormat("#.##");
        String strValue = df.format(value);

        // Thay thế dấu chấm và dấu phẩy nếu có
        strValue = strValue.replace(".", "").replace(",", "");

       return strValue;
        // Nhân với 1000 để chuyển từ đơn vị nghìn đồng sang đơn vị đồng
//        long amountInVND = (long) (value * 1000);
//        return Long.toString(amountInVND);

    }












}
