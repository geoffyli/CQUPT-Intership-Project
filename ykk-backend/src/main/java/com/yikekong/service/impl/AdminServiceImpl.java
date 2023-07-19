package com.yikekong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.yikekong.entity.AdminEntity;
import com.yikekong.mapper.AdminMapper;
import com.yikekong.service.AdminService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper,AdminEntity> implements AdminService{
    @Override
    public Integer login(String loginName, String password) {
        // Non-empty check
        if(Strings.isNullOrEmpty(loginName) || Strings.isNullOrEmpty(password)){
            return -1;
        }
        // Query the database for the user with the specified login name and password to see if it exists.
        QueryWrapper<AdminEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .lambda()
                .eq(AdminEntity::getLoginName,loginName);
        AdminEntity adminEntity = this.getOne(queryWrapper);
        // If the username does not exist, return -1
        if(adminEntity == null)
            return -1;
        // If the username exists, compare the password to see if it matches.
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(password,adminEntity.getPassword())){
            // If the password matches, return the user id.
            return adminEntity.getId();
        }
        // If the password does not match, return -1
        return -1;
    }
}
