package com.yikekong.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PreviewVO implements Serializable {

    private String quotaId;

    private List<String> deviceIdList;

    private Integer type;

    private String start;

    private String end;

}
