package com.sensonet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sensonet.mapper.entity.QuotaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuotaMapper extends BaseMapper<QuotaEntity>{

    /**
     * Select by subject
     * @param subject The subject
     * @return The quota list
     */
    @Select("select * from tb_quota where subject=#{subject} ")
    List<QuotaEntity> selectBySubject(String subject);

    /**
     * Get the number of quotas
     * @return The number of quotas
     */
    @Select("select count(*) from tb_quota")
    int getQuotaCount();
}