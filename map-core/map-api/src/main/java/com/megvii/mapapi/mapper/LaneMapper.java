package com.megvii.mapapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.megvii.mapapi.entity.LaneEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@Mapper
public interface LaneMapper extends BaseMapper<LaneEntity> {
}
