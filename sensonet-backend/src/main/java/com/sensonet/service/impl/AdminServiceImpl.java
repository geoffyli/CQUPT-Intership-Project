package com.sensonet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.sensonet.mapper.entity.AdminEntity;
import com.sensonet.mapper.AdminMapper;
import com.sensonet.service.AdminService;
import com.sensonet.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements AdminService {
    @Override
    public Integer login(String adminName, String password) {
        // Non-empty check
        if (Strings.isNullOrEmpty(adminName) || Strings.isNullOrEmpty(password)) {
            return -1;
        }
        // Query the database for the user with the specified login name and password to see if it exists.
        QueryWrapper<AdminEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(AdminEntity::getAdminName, adminName);
        AdminEntity adminEntity = this.getOne(queryWrapper);
        // If the username does not exist, return -1
        if (adminEntity == null)
            return -1;
        // If the username exists, compare the password to see if it matches.
        if (password.equals(adminEntity.getPassword())) {
            // If the password matches, return the user id.
            return adminEntity.getId();
        }
        // If the password does not match, return -1
        return -1;
    }

    @Override
    public Integer addAdmin(AdminEntity adminEntity) {
        // Non-empty check
        if (Strings.isNullOrEmpty(adminEntity.getAdminName()) || Strings.isNullOrEmpty(adminEntity.getPassword())) {
            return -1;
        }
        // Query the database for the user with the specified login name to see if it exists.
        QueryWrapper<AdminEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(AdminEntity::getAdminName, adminEntity.getAdminName());
        AdminEntity adminEntity1 = this.getOne(queryWrapper);
        // If the username exists, return -1
        if (adminEntity1 != null)
            return -1;
        // If the username does not exist, add the user to the database.
        this.save(adminEntity);
        // Return the user id.
        return adminEntity.getId();
    }

    @Override
    public void logout(String token) {
        // Invalidate the token
        JwtUtil.invalidateJWT(token);
    }
}
