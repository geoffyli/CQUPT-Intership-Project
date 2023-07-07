package com.yikekong.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BoardQuotaData implements Serializable {

    private String name;

    private List<Double> data;

}
