package com.megvii.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.megvii.entity.PointEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@Mapper
public interface PointMapper extends BaseMapper<PointEntity> {
    void insertBatch(List<PointEntity> pointEntityList);

}