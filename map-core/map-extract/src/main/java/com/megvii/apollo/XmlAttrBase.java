package com.megvii.apollo;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.megvii.doc.ObjPointsDoc;
import com.megvii.doc.PointDoc;
import com.megvii.elamapper.ObjPointsMapper;
import com.megvii.elamapper.PointDocMapper;
import com.megvii.service.PointService;
import com.megvii.utils.ApolloPointUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public abstract class XmlAttrBase<T> {
    @Autowired
    protected ApolloPointUtils apolloPointUtils;
    @Autowired
    protected PointService pointService;
    @Autowired
    protected PointDocMapper pointDocMapper;
    @Resource
    protected ObjPointsMapper objPointsMapper;

    protected String xmlUid;

    protected T entityList;
    protected Integer nodeNum;

    protected final Integer BATCH_SIZE = 5;

    protected AtomicInteger cuurIndex = new AtomicInteger(0);

    protected Long pointDocCount = 0L;

    protected CopyOnWriteArrayList<ObjPointsDoc> objPointsDocList = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<PointDoc> pointDocList = new CopyOnWriteArrayList<>();

    public abstract XmlAttrBase<T> getAttrEntity(List<Node> nodeList);

    public abstract void saveToDb() throws IOException;

    protected ConcurrentHashMap<String, String> roadType;

    public XmlAttrBase<T> setRoadType(ConcurrentHashMap<String, String> roadType) {
        this.roadType = roadType;
        return this;
    }

    public XmlAttrBase<T> setXmlUid(String xmlUid) {
        this.xmlUid = xmlUid;
        return this;
    }

    public void overOpt() {
        log.info("------------------本次所有point插入完成！！！共" + pointDocCount + "个点-------------------");
        pointDocCount = 0L;
        cuurIndex = new AtomicInteger(0);
        pointDocList.clear();
    }

    private synchronized void savePointToEs() {
        cuurIndex.getAndAdd(1);
        if (pointDocList.size() == 0)
            return;
        if (cuurIndex.get() % BATCH_SIZE == 0 || cuurIndex.get() == nodeNum) {
            if (cuurIndex.get() == nodeNum) {
                log.info("最后一个批次插入，当前位置" + cuurIndex + "总条数" + nodeNum);
            }
            try {
                pointDocMapper.saveBulk(pointDocList);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            pointDocCount += pointDocList.size();
            pointDocList.clear();
        }
    }

    private synchronized void saveObjPointsToEs() {
        try {
            BulkResponse bulkResponse = objPointsMapper.saveBulk(objPointsDocList);
            System.out.println(bulkResponse);
            cuurIndex.getAndAdd(1);
            objPointsDocList.clear();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void getObjPointsAndSaveToEs(List<Element> elements, Integer step, String objId, String type) {
        if (elements.size() == 0)
            return;
        ObjPointsDoc gcjObjPoints = apolloPointUtils.getGcjObjPoints(elements, step, objId, xmlUid, type);
        objPointsDocList.add(gcjObjPoints);
        saveObjPointsToEs();
    }

    protected void getGcjPointListAndSaveToEs(List<Element> elements, Integer step, String objId, String type, Boolean refLane) {
        if (refLane) {
            getObjPointsAndSaveToEs(elements, step, objId, type);
        } else {
            if (elements.size() == 0){
                System.out.println("error nodes uid:" +objId);
                return;
            }
            List<PointDoc> gcjPointDocsList = apolloPointUtils.getGcjPointDocsList(elements, step, objId, xmlUid, type);
            List<PointDoc> gcjPointDocsWithRefLaneList = gcjPointDocsList.parallelStream().peek(pointDoc -> {
                pointDoc.setRefLane(refLane);
            }).collect(Collectors.toList());
            System.out.println(gcjPointDocsWithRefLaneList);
            pointDocList.addAll(gcjPointDocsWithRefLaneList);
            savePointToEs();
        }
    }


    protected void getGcjPointListAndSaveToEs(List<Element> elements, Integer step, String objId, String type) {
        if (elements.size() == 0)
            return;
        List<PointDoc> gcjPointDocsList = apolloPointUtils.getGcjPointDocsList(elements, step, objId, xmlUid, type);
        pointDocList.addAll(gcjPointDocsList);
        savePointToEs();
    }
}
