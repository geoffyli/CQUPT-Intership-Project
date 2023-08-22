package com.sensonet.controller;

import com.sensonet.entity.AdminEntity;
import com.sensonet.vo.AdminVO;
import com.sensonet.vo.LoginResultVO;
import com.sensonet.service.AdminService;
import com.sensonet.util.JwtUtil;
import com.sensonet.vo.LogoutResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public LoginResultVO login(@RequestBody AdminVO admin) {

        // Call the login method of the AdminService interface
        Integer adminId = adminService.login(admin.getAdminName(), admin.getPassword());
        // If the login fails, set the loginSuccess attribute of the LoginResultVO object to false and return the LoginResultVO object.
        return setLoginResultVO(adminId);
    }

    @PostMapping("/signup")
    public LoginResultVO signup(@RequestBody AdminVO admin) {
        // Create an admin entity
        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setAdminName(admin.getAdminName());
        adminEntity.setPassword(admin.getPassword());
        // Add admin
        Integer adminId = adminService.addAdmin(adminEntity);
        return setLoginResultVO(adminId);
    }

    @PostMapping("/logout")
    public LogoutResultVO logout(ServletRequest servletRequest) {
        // Get authorization token from request header
        String token = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        // Call the logout method of the AdminService interface
        adminService.logout(token);
        LogoutResultVO result = new LogoutResultVO();
        result.setLogoutSuccess(true);
        result.setToken(token);
        return result;
    }

    private LoginResultVO setLoginResultVO(Integer adminId) {
        // Create a new LoginResultVO object
        LoginResultVO result = new LoginResultVO();
        if (adminId < 0) {
            result.setLoginSuccess(false);
            return result;
        }
        // Log in
        result.setAdminId(adminId);
        String token = JwtUtil.createJWT(adminId);
        result.setToken(token);
        result.setLoginSuccess(true);

        return result;
    }
}
