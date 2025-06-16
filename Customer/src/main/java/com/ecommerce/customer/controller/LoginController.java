package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.AdminService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.utils.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class LoginController {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder passwordEncoder;
    private  final CommonUtil commonUtil;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        model.addAttribute("page", "Home");
        return "login";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("page", "Register");
        model.addAttribute("customerDto", new CustomerDto());
        return "register";
    }


    @PostMapping("/do-register")
    public String registerCustomer(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   BindingResult result,
                                   Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
            String username = customerDto.getUsername();
            Customer customer = customerService.findByUsername(username);
            if (customer != null) {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("error", "Email has been register!");
                return "register";
            }
            if (customerDto.getPassword().equals(customerDto.getConfirmPassword())) {
                customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
                customerService.save(customerDto);
                model.addAttribute("success", "Register successfully!");
            } else {
                model.addAttribute("error", "Password is incorrect");
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server is error, try again later!");
        }
        return "register";
    }
    //forgot -password
//    @GetMapping("/forgot-password")
//    public String forgotPassword(Model model) {
//        model.addAttribute("title", "Forgot Password");
//        return "h111";
//
//    }
//    @PostMapping("/forgot-password")
//    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
//            throws UnsupportedEncodingException, MessagingException {
//
//        Customer userByEmail = customerService.findByUsername(email);
//
//        if (ObjectUtils.isEmpty(userByEmail)) {
//            session.setAttribute("errorMsg", "Invalid email");
//        } else {
//
//            String resetToken = UUID.randomUUID().toString();
////           customerService.updateUserResetToken(email, resetToken);
//
//            // Generate URL :
//            // http://localhost:8019/reset-password?token=sfgdbgfswegfbdgfewgvsrg
//
//            String url = CommonUtil.generateUrl(request) + "/h111?token=" + resetToken;
//
//            Boolean sendMail = commonUtil.sendMail(url, email);
//
//            if (sendMail) {
//                session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
//            } else {
//                session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
//            }
//        }
//
//        return "redirect:/h111";
//    }
//
//
//    @GetMapping("/reset-password")
//    public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {
//
////        Customer userByToken = customerService.getUserByToken(token);
//        Customer userByToken = customerService.findByUsername(token);
//
//        if (userByToken == null) {
//            m.addAttribute("msg", "Your link is invalid or expired !!");
//            return "message";
////            return "login";
//        }
//        m.addAttribute("token", token);
//        return "h1111";
//    }

    //    @PostMapping("/reset-password")
//    public String changePass(@RequestParam("oldPassword") String oldPassword,
//                             @RequestParam("newPassword") String newPassword,
//                             @RequestParam("repeatNewPassword") String repeatPassword,
//                             RedirectAttributes attributes,
//                             Model model,
//                             Principal principal) {
//        if (principal == null) {
//            return "redirect:/login";
//        } else {
//            AdminDto admin = adminService.getAdmin(principal.getName());
//            if (passwordEncoder.matches(oldPassword, admin.getPassword())
//                    && !passwordEncoder.matches(newPassword, oldPassword)
//                    && !passwordEncoder.matches(newPassword, admin.getPassword())
//                    && repeatPassword.equals(newPassword) && newPassword.length() >= 5) {
//                admin.setPassword(passwordEncoder.encode(newPassword));
//                adminService.changePass(admin);
//                attributes.addFlashAttribute("success", "Your password has been changed successfully!");
//                return "redirect:/login";
//            } else {
//                model.addAttribute("message", "Your password is wrong");
//                return "reset-password";
//            }
//        }
//    }
//    @PostMapping("/reset-password")
//    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
//                                Model m) {
//
////        Customer userByToken = customerService.getUserByToken(token);
//                Customer userByToken = customerService.findByUsername(token);
//
//        if (userByToken == null) {
//            m.addAttribute("errorMsg", "Your link is invalid or expired !!");
//            return "login";
//        } else {
////            userByToken.setPassword(passwordEncoder.encode(password));
////            userByToken.setResetToken(null);
////            adminService.save(userByToken);
////            // session.setAttribute("succMsg", "Password change successfully");
////            m.addAttribute("msg", "Password change successfully");
//
//            return "login";
//        }
//
//    }

}
