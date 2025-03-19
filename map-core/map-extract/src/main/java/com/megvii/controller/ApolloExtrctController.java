package com.megvii.controller;

import com.megvii.service.ExtractService;
import com.megvii.service.XmlService;
import com.megvii.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/map/extract/v1/apollo")
@Slf4j
public class ApolloExtrctController {
    @Resource
    private ExtractService extractService;
    @Resource
    private XmlService xmlService;

    @GetMapping("/saveXml")
    public R saveXml(@RequestParam(name = "ossPath", defaultValue = "null") String ossPath,
                     @RequestParam(name = "remark", required = false, defaultValue = "null") String remark) throws DocumentException {
        ArrayList<String> arrayList = new ArrayList<>(List.of(ossPath.split("/")));
        String xml = arrayList.get(arrayList.size() - 1).replace(".xml","");
        log.info("数据开始落库：" + xml);
        try {
            if ("null".equals(remark))
                remark = xml;
            extractService.saveXmlFromFile(xml, remark, ossPath);
            log.info("数据落库成功：" + xml);
            return R.ok().put("data", xml + "落库成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(xml + "落库失败！！！");
            log.info(xml + " Delete/Insert Operation Rollback");
            log.info(e.toString());
            return R.error(xml + "落库失败！！！").put("exception", e.getMessage());
        }
    }

    /**
     * 从数据库删除xml文件
     * @param remark 地图的别名
     * @return
     */
    @GetMapping("/delXml")
    public Integer delXml(@RequestParam("remark") String remark) {
        xmlService.delByRemark(remark);
        return 200;
    }
}
