package com.yikekong.vo;

import com.yikekong.dto.DeviceDTO;
import lombok.Data;

import java.util.List;

@Data
public class DeviceListVO {
    private List<DeviceDTO> items;

}
