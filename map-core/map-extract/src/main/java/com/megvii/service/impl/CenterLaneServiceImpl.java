package com.megvii.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.entity.CenterLaneEntity;
import com.megvii.mapper.CenterLaneMapper;
import com.megvii.service.CenterLaneService;
import org.springframework.stereotype.Service;

@Service("centerLaneService")
public class CenterLaneServiceImpl extends ServiceImpl<CenterLaneMapper, CenterLaneEntity> implements CenterLaneService {

}