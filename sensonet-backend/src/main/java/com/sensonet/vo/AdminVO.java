package com.sensonet.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminVO implements Serializable{
    private String adminName;
    private String password;
}
