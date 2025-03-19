package com.megvii.utils;

import com.alibaba.fastjson.JSONArray;
import com.megvii.doc.PointDoc;
import com.megvii.entity.PointEntity;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKBWriter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ApolloBaseUtils {

    @Resource
    private TransformUtil transformUtil;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public List<JSONArray> getGcjPoints(List<Element> points, int step) {
        AtomicInteger index = new AtomicInteger(0);
        return points.stream().filter(x -> {
            int i = index.get();
            if (i == 0)
                return true;
            else if (i == points.size())
                return true;
            else
                return i % step == 0;
        }).map(point -> {
            String lon = point.attributeValue("x");
            String lat = point.attributeValue("y");
            List<Double> mgLonLat = transformUtil.wgs2gcj(lon, lat);
            JSONArray lonLat = new JSONArray();
            lonLat.add(mgLonLat.get(0).toString());
            lonLat.add(mgLonLat.get(1).toString());
            index.getAndSet(index.get());
            return lonLat;
        }).collect(Collectors.toList());
    }

    public String getUUID(String xmlUid) {
        String uuid = String.valueOf(xmlUid.hashCode()).replace("-", "").substring(0, 4);
        String ts = String.valueOf(System.currentTimeMillis()).substring(10);
        String uuid2 = UUID.randomUUID().toString().replace("-", "").substring(12, 25);
        return uuid + ts + uuid2;
    }

    private String getPointDocId(PointDoc pointDoc) {
        String hashCode = String.valueOf(pointDoc.hashCode()).replace("-", "");
        String ts = String.valueOf(System.currentTimeMillis()).substring(10);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(12, 25);
        return ts + uuid + hashCode;
    }

    public List<PointDoc> getPointDocsList(List<JSONArray> gcjPointList, String objUid, String xmlUid, String type) {
        ArrayList<PointDoc> pointEntities = new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        gcjPointList.forEach(gcjPoint -> {
            PointDoc pointDoc = new PointDoc();
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(new BigDecimal(gcjPoint.get(0).toString()));
            jsonArray.add(new BigDecimal(gcjPoint.get(1).toString()));
            pointDoc.setGeo(jsonArray);
            pointDoc.setIndex(index.get());
            pointDoc.setObjUid(objUid);
            pointDoc.setXmlUid(xmlUid);
            pointDoc.setType(type);
            pointDoc.setId(getPointDocId(pointDoc));
            pointEntities.add(pointDoc);
            index.addAndGet(1);
        });
        return pointEntities;
    }

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public List<PointEntity> getPointsList(List<JSONArray> gcjPointList, String laneUid, String xmlUid) {
        ArrayList<PointEntity> pointEntities = new ArrayList<>();
        AtomicLong index = new AtomicLong();
        gcjPointList.forEach(gcjPoint -> {
            PointEntity pointEntity = new PointEntity();
            pointEntity.setXmlUid(xmlUid);
            pointEntity.setLaneUid(laneUid);
            pointEntity.setPIndex(index.get());
            double doubleLon = Double.parseDouble(gcjPoint.get(0).toString());
            double doubleLat = Double.parseDouble(gcjPoint.get(1).toString());
            pointEntity.setLon(BigDecimal.valueOf(doubleLon));
            pointEntity.setLat(BigDecimal.valueOf(doubleLat));
            Coordinate coordinate = new Coordinate(doubleLon, doubleLat);
            Point point = geometryFactory.createPoint(coordinate);

            WKBWriter wkbWriter = new WKBWriter();
            byte[] write = wkbWriter.write(point);
            String geoHex = WKBWriter.toHex(write);

//            pointEntity.setGeo(geoHex);
            pointEntity.setGeo(point.toText());
            pointEntities.add(pointEntity);
            index.addAndGet(1);
        });
        return pointEntities;
    }


//    public PointEntity getPointEntity(List<JSONArray> pointList, String xmlUid) {
//        PointEntity pointEntity = new PointEntity();
//        pointEntity.setPoints(pointList.toString());
//        pointEntity.setXmlUid(xmlUid);
//        String uuid = null;
//        Boolean member = null;
//        while (member == null || member) {
//            uuid = getPointUUID();
//            member = redisTemplate.opsForSet().isMember("uuidSet", uuid);
//        }
//        redisTemplate.opsForSet().add("uuidSet", uuid);
//        pointEntity.setPointsUid(uuid);
//        return pointEntity;
//    }
}
