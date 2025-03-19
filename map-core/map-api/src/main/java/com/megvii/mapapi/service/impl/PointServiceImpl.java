package com.megvii.mapapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.mapapi.entity.PointEntity;
import com.megvii.mapapi.mapper.PointMapper;
import com.megvii.mapapi.service.PointService;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, PointEntity> implements PointService {
}
