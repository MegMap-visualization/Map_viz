package com.megvii.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.entity.ObjectEntity;
import com.megvii.mapper.ObjectMapper;
import com.megvii.service.ObjectService;
import org.springframework.stereotype.Service;



@Service("objectOutlineService")
public class ObjectServiceImpl extends ServiceImpl<ObjectMapper, ObjectEntity> implements ObjectService {

}