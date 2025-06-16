package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.service.AdminService;
import com.ecommerce.library.utils.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AdminService adminService;

    private final BCryptPasswordEncoder passwordEncoder;

    private  final CommonUtil commonUtil;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }

    @RequestMapping("/404")
    public String errror(Model model) {
        return "404";
    }
    @RequestMapping("/blank")
    public String blank(Model model) {
        return "blank";
    }


    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("adminDto", new AdminDto());
        return "register";
    }


    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult result,
                              Model model) {

        try {

            if (result.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                return "register";
            }
            String username = adminDto.getUsername();
            Admin admin = adminService.findByUsername(username);
            if (admin != null) {
                model.addAttribute("adminDto", adminDto);
                System.out.println("admin not null");
                model.addAttribute("emailError", "Your email has been registered!");
                return "register";
            }
            if (adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                System.out.println("success");
                model.addAttribute("success", "Register successfully!");
                model.addAttribute("adminDto", adminDto);
            } else {
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
                System.out.println("password not same");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "The server has been wrong!");
        }
        return "register";

    }
    //forgot -password
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";

    }
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {

        Admin userByEmail = adminService.findByUsername(email);

        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Invalid email");
        } else {

            String resetToken = UUID.randomUUID().toString();
            adminService.updateUserResetToken(email, resetToken);

            // Generate URL :
            // http://localhost:8019/reset-password?token=sfgdbgfswegfbdgfewgvsrg

            String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

            Boolean sendMail = commonUtil.sendMail(url, email);

            if (sendMail) {
                session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
            } else {
                session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
            }
        }

        return "redirect:/forgot-password";
    }


    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

        Admin userByToken = adminService.getUserByToken(token);

        if (userByToken == null) {
            m.addAttribute("msg", "Your link is invalid or expired !!");
            return "message";
//            return "login";
        }
        m.addAttribute("token", token);
        return "reset-password";
    }

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
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
                            Model m) {

        Admin userByToken = adminService.getUserByToken(token);
     if (userByToken == null) {
        m.addAttribute("errorMsg", "Your link is invalid or expired !!");
        return "login";
    } else {
        userByToken.setPassword(passwordEncoder.encode(password));
        userByToken.setResetToken(null);
        adminService.save(userByToken);
        // session.setAttribute("succMsg", "Password change successfully");
        m.addAttribute("msg", "Password change successfully");

        return "login";
    }

}

}
