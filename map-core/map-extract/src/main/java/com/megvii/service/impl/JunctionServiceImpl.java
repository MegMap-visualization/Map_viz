package com.megvii.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.entity.JunctionEntity;
import com.megvii.mapper.JunctionMapper;
import com.megvii.service.JunctionService;
import org.springframework.stereotype.Service;


@Service("junctionService")
public class JunctionServiceImpl extends ServiceImpl<JunctionMapper, JunctionEntity> implements JunctionService {

}