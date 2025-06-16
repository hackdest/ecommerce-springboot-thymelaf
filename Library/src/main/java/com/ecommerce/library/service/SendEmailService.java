//package com.ecommerce.library.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
////import org.springframework.mail.MailSender;
//import org.springframework.mail.*;
//
//@Service
//public class SendEmailService {
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendEmail(String email, String subject, String message) {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(email);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(message);
//        mailSender.send(mailMessage);
//    }
//}