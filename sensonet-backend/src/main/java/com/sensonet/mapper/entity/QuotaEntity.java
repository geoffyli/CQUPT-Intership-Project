package com.sensonet.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "tb_quota")
@Data
public class QuotaEntity{
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     * The name of the quota
     */
    private String name;
    /**
     * The unit of the quota
     */
    private String unit;
    /**
     * The subject of the quota
     */
    private String subject;
    /**
     * The value key of the quota in the payload
     */
    private String valueKey;
    /**
     * the value type of the quota
     */
    private String valueType;
    /**
     * the device identification key in the payload
     */
    private String snKey;
    /**
     * the webhook
     */
    private String webhook;
    /**
     * reference value
     */
    private String referenceValue;
}
