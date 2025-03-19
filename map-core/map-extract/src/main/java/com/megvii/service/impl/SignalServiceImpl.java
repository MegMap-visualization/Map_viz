package com.megvii.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.entity.SignalEntity;
import com.megvii.mapper.SignalMapper;
import com.megvii.service.SignalService;
import org.springframework.stereotype.Service;


@Service("signalService")
public class SignalServiceImpl extends ServiceImpl<SignalMapper, SignalEntity> implements SignalService {

}