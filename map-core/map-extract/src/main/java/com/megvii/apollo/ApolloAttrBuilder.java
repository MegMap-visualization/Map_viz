package com.megvii.apollo;

import com.megvii.utils.RoadTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class ApolloAttrBuilder {
    @Resource
    private LaneXmlAttr laneXmlAttr;
    @Resource
    private JunctionXmlAttr junctionXmlAttr;
    @Resource
    private ObjectXmlAttr objectXmlAttr;
    @Resource
    private SignalXmlAttr signalXmlAttr;
    @Resource
    private CenterLaneXmlAttr centerLaneXmlAttr;
    @Resource
    private RoadTypeUtil roadTypeUtil;

    public void builderAll(Document document, String xml) throws IOException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start(xml);
        ConcurrentHashMap<String, String> roadType = roadTypeUtil.getRoadType(document.selectNodes("//road"));
        laneXmlAttr.setRoadType(roadType);
        List<Node> nodeList = document.selectNodes("//lane");
        centerLaneXmlAttr.setXmlUid(xml).getAttrEntity(nodeList).saveToDb();
        junctionXmlAttr.setXmlUid(xml).getAttrEntity(document.selectNodes("//junction")).saveToDb();
        objectXmlAttr.setXmlUid(xml).getAttrEntity(document.selectNodes("//object")).saveToDb();
        signalXmlAttr.setXmlUid(xml).getAttrEntity(document.selectNodes("//signal")).saveToDb();
        laneXmlAttr.setXmlUid(xml).getAttrEntity(nodeList).saveToDb();
        stopWatch.stop();
        log.info("本次数据提取共耗时" + stopWatch.getTotalTimeSeconds());
    }
}
