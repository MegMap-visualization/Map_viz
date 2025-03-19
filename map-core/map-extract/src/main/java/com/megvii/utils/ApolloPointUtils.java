package com.megvii.utils;

import com.alibaba.fastjson.JSONArray;
import com.megvii.doc.ObjPointsDoc;
import com.megvii.doc.PointDoc;
import com.megvii.entity.PointEntity;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Component
public class ApolloPointUtils {
    @Resource
    private ApolloBaseUtils apolloBaseUtils;

    public List<PointEntity> getGcjPointsList(List<Element> points, int step, String laneId, String xmlUid) {
        List<JSONArray> gcjPoints = apolloBaseUtils.getGcjPoints(points, step);
        return apolloBaseUtils.getPointsList(gcjPoints, laneId, xmlUid);
    }

    public List<PointDoc> getGcjPointDocsList(List<Element> points, int step, String objUid, String xmlUid, String type) {
        List<JSONArray> gcjPoints = apolloBaseUtils.getGcjPoints(points, step);
        return apolloBaseUtils.getPointDocsList(gcjPoints, objUid, xmlUid, type);
    }

    public ObjPointsDoc getGcjObjPoints(List<Element> points, int step, String objUid, String xmlUid, String type) {
        List<JSONArray> gcjPoints = apolloBaseUtils.getGcjPoints(points, step);
        ObjPointsDoc objPointsDoc = new ObjPointsDoc();
        objPointsDoc.setPoints(new JSONArray(Collections.singletonList(gcjPoints)));
        objPointsDoc.setObjUid(objUid);
        objPointsDoc.setXmlUid(xmlUid);
        objPointsDoc.setType(type);
        return objPointsDoc;
    }
}
