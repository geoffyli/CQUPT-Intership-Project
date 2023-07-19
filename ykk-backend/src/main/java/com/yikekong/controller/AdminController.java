package com.yikekong.controller;

import com.yikekong.vo.AdminVO;
import com.yikekong.vo.LoginResultVO;
import com.yikekong.service.AdminService;
import com.yikekong.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController{
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public LoginResultVO login(@RequestBody AdminVO admin){
        // Create a new LoginResultVO object
        LoginResultVO result = new LoginResultVO();
        // Call the login method of the AdminService interface
        Integer adminId = adminService.login(admin.getLoginName(),admin.getPassword());
        // If the login fails, set the loginSuccess attribute of the LoginResultVO object to false and return the LoginResultVO object.
        if(adminId < 0){
            result.setLoginSuccess(false);
            return result;
        }
        result.setAdminId(adminId);
        // If the login is successful, create a token and set the loginSuccess attribute of the LoginResultVO object to true and return the LoginResultVO object.
        String token = JwtUtil.createJWT(adminId);
        // Set the token attribute of the LoginResultVO object to the token just created.
        result.setToken(token);
        result.setLoginSuccess(true);

        return result;
    }
}
