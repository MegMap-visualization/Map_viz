package com.megvii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.megvii.entity.ObjectEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ObjectMapper extends BaseMapper<ObjectEntity> {
	
}
