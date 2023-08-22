package com.sensonet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sensonet.entity.GPSEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GpsMapper extends BaseMapper<GPSEntity>{
}
