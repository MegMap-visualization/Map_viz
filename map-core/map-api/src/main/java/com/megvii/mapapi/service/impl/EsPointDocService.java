package com.megvii.mapapi.service.impl;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.JSONArray;
import com.megvii.doc.PointDoc;
import com.megvii.elamapper.PointDocMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EsPointDocService {
    @Autowired
    private PointDocMapper pointDocMapper;

    private JSONArray parseAllPointDocsPointsArray(List<PointDoc> pointDocs) {
        CopyOnWriteArrayList<JSONArray> jsonArrays = new CopyOnWriteArrayList<>();
        pointDocs.parallelStream().forEach(pointDoc -> jsonArrays.add(pointDoc.getGeo()));
        return new JSONArray(Collections.singletonList(jsonArrays)).getJSONArray(0);
    }

    private Map<String, Map<String, JSONArray>> parseHitsGroupByTypeAndObjUid(List<PointDoc> pointDocList) {
        Map<String, Map<String, TreeMap<Integer, List<PointDoc>>>> collect = pointDocList.stream().collect(Collectors.groupingBy(PointDoc::getType, Collectors.groupingBy(PointDoc::getObjUid,
                Collectors.groupingBy(
                        PointDoc::getIndex, TreeMap::new, Collectors.toList()
                ))));
        Map<String, Map<String, JSONArray>> geoGroupByTypeAndObjectId = new HashMap<>();
        collect.forEach((type, v) -> {
            Map<String, JSONArray> geoGroupByObjectId = new HashMap<>();
            v.forEach((objectId, v1) -> {
                JSONArray tempObjPoints = new JSONArray();
                v1.forEach((index, pl) -> tempObjPoints.add(pl.get(0).getGeo()));
                geoGroupByObjectId.put(objectId, tempObjPoints);
            });
            geoGroupByTypeAndObjectId.put(type, geoGroupByObjectId);
        });
        return geoGroupByTypeAndObjectId;
    }

    public JSONArray getObjectPointsArrayFromEs(Map<String, List<String>> searchMap) throws IOException {
        log.info("ElasticSearch InterfaceName: searchByKeysTerms Args: " + searchMap.toString());
        ArrayList<PointDoc> pointDocs = pointDocMapper.searchByKeysTerms(searchMap);
        return parseAllPointDocsPointsArray(pointDocs);
    }

    public Map<String, Map<String, JSONArray>> searchByKeyAndBoundingBox(Map<String, List<String>> reqMap, List<Double> ltLonLat, List<Double> rbLonlat) throws IOException {
        ArrayList<PointDoc> pointDocs = pointDocMapper.searchByKeyAndBoundingBox(reqMap, ltLonLat, rbLonlat);
        return parseHitsGroupByTypeAndObjUid(pointDocs);
    }

    public Map<String, Map<String, JSONArray>> searchByKeysTerms(Map<String, List<String>> searchMap) throws IOException {
        log.info("ElasticSearch InterfaceName: searchByKeysTerms Args: " + searchMap.toString());
        ArrayList<PointDoc> pointDocs = pointDocMapper.searchByKeysTerms(searchMap);
        return parseHitsGroupByTypeAndObjUid(pointDocs);
    }
}
