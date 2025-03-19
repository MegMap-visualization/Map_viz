package com.megvii.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.entity.PointEntity;
import com.megvii.mapper.PointMapper;
import com.megvii.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PointServiceImpl extends ServiceImpl<PointMapper, PointEntity> implements PointService {

    private StopWatch stopWatch = new StopWatch();

    @Override
    public boolean saveBatch(Collection<PointEntity> entityList, int batchSize) {
        log.info("本次任务Points数据总条数：" + entityList.size() + " 批次大小设置:" + batchSize + "插入次数：：" + entityList.size() / batchSize);
        stopWatch.start();
        ArrayList<PointEntity> pointEntities = new ArrayList<>(entityList);
        int count = 1;
        while (pointEntities.size() != 0) {
            List<PointEntity> collect = pointEntities.stream().limit(batchSize).collect(Collectors.toList());
            baseMapper.insertBatch(collect);
            pointEntities.removeAll(collect);
            log.info("第" + count++ + "批次写入完成");
        }
        stopWatch.stop();
        log.info("本次Points数据插入耗时： " + stopWatch.getTotalTimeSeconds());
        return true;
    }
}
