package com.yikekong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yikekong.entity.AdminEntity;

public interface AdminService extends IService<AdminEntity>{
    /**
     * Admin login
     * @param loginName the login name
     * @param password the password
     * @return the admin id
     */
    Integer login(String loginName,String password);

    /**
     * Add admin
     * @param adminEntity the admin entity
     * @return the admin id
     */
    Integer addAdmin(AdminEntity adminEntity);

    /**
     * Admin logout
     * @param token the token
     */
    void logout(String token);
}
