package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.repository.AdminRepository;
import com.ecommerce.library.repository.RoleRepository;
import com.ecommerce.library.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;


    @Override
    public Admin save(AdminDto adminDto) {
        Admin admin = new Admin();
        admin.setFirstName(adminDto.getFirstName());
        admin.setLastName(adminDto.getLastName());
        admin.setUsername(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        admin.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));
        return adminRepository.save(admin);
    }
    @Override
    public AdminDto getAdmin(String username) {
        AdminDto adminDto = new AdminDto();
        Admin admin = adminRepository.findByUsername(username);
        adminDto.setFirstName(admin.getFirstName());
        adminDto.setLastName(admin.getLastName());
        adminDto.setUsername(admin.getUsername());
        adminDto.setPassword(admin.getPassword());
        return adminDto;
    }

    @Override
    public Admin getUserByToken(String token) {
        return adminRepository.findByResetToken(token);
    }

    @Override
    public void updateUserResetToken(String username, String resetToken) {
        Admin findByEmail = adminRepository.findByUsername(username);
        findByEmail.setResetToken(resetToken);
        adminRepository.save(findByEmail);
    }

    @Override
    public Admin save(Admin admin) {
        return adminRepository.save(admin);

    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public Admin changePass(AdminDto adminDto) {
        Admin admin = adminRepository.findByUsername(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        return adminRepository.save(admin);
    }


}
