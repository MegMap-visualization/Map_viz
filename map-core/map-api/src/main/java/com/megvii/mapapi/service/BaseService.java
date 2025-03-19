package com.megvii.mapapi.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.megvii.doc.PointDoc;
import com.megvii.elamapper.PointDocMapper;
import com.megvii.exception.CommonException;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    default T getInfoByXmlUidAndObjId(String objKey, String objValue, String xmlUid) {
        return getBaseMapper().selectOne(new QueryWrapper<T>()
                .eq("xml_uid", xmlUid)
                .eq(objKey, objValue));
    }

    default List<T> getEntitesWithPointsInfoFromMap(Map<String, JSONArray> entitys, String xmlUid) {
        throw new CommonException("Please Override This Method");
    }
}
