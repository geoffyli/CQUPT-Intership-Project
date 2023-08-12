package com.yikekong.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogoutResultVO implements Serializable {
    /**
     * Log out result
     */
    private Boolean logoutSuccess;
    /**
     * jwt token
     */
    private String token;
}
