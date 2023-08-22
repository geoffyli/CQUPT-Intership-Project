package com.sensonet.vo;

import com.sensonet.dto.DeviceDTO;
import lombok.Data;

import java.util.List;

@Data
public class DeviceListVO {
    private List<DeviceDTO> items;

}
