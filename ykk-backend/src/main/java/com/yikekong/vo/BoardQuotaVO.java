package com.yikekong.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BoardQuotaVO implements Serializable {

    private List<String> xdata;//横轴

    private List<BoardQuotaData> series;  //数据

    private String name;//面板名称

}



