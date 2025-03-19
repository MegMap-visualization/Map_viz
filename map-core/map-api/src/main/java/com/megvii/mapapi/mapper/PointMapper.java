package com.megvii.mapapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.megvii.mapapi.entity.PointEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@Mapper
public interface PointMapper extends BaseMapper<PointEntity> {

    List<PointEntity> selectPointsFromXy();
}