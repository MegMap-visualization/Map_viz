package com.megvii.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseService<T> extends IService<T> {
    default List<T> baseGetEntityList(String column, String val) {
        return getBaseMapper().selectList(new QueryWrapper<T>().eq(column, val));
    }

    default T baseGetOneEntityByColumn(String column, String val) {
        return getBaseMapper().selectOne(new QueryWrapper<T>().eq(column, val));
    }

    default void baseRemoveByXmlUid(String xmlUid) {
        getBaseMapper().delete(new QueryWrapper<T>().eq("xml_uid", xmlUid));
    }
}
