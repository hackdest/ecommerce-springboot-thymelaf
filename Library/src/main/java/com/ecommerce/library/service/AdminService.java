package com.ecommerce.library.service;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Customer;

public interface AdminService {
    Admin save(AdminDto adminDto);

    Admin findByUsername(String username);
    Admin changePass(AdminDto adminDto);

    AdminDto getAdmin(String username);
    Admin getUserByToken(String token);
   void updateUserResetToken(String username, String resetToken);

    Admin save(Admin admin);


}
