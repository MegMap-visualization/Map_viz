package com.megvii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.megvii.apollo.ApolloAttrBuilder;
import com.megvii.elamapper.ObjPointsMapper;
import com.megvii.utils.AwsS3Util;
import com.megvii.utils.Md5Util;
import com.megvii.elamapper.PointDocMapper;
import com.megvii.entity.*;
import com.megvii.service.*;
import com.megvii.utils.ApolloBaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@Service
@Slf4j
public class ExtractServiceImpl implements ExtractService {
    private final SAXReader saxReader = new SAXReader();
    @Resource
    private ApolloBaseUtils apolloBaseUtils;
    @Resource
    private ApolloAttrBuilder apolloAttrBuilder;
    @Resource
    private XmlService xmlService;
    @Autowired
    private PointDocMapper pointDocMapper;
    @Resource
    private ObjPointsMapper objPointsMapper;
    @Autowired
    private AwsS3Util awsS3Util;

    private File getFileFromOss(String ossPath) {
        String[] split = ossPath.replace("s3://", "").split("/");
        String bucket_name = split[0];
        String file_name = split[split.length - 1];
        String key = String.join("/", Arrays.copyOfRange(split, 1, split.length));
        File file = new File(file_name);
        if (file.exists()) {
            file.delete();
        }
        log.info("Start download file "+ossPath);
        try {
            awsS3Util.init().downloadFile(bucket_name, key, file);
        } catch (URISyntaxException e) {
            log.info("Init AwsS3Client failed");
            throw new RuntimeException(e);
        }
        log.info("Fininsh download file");
        return file;
    }

    @Override
    @Transactional
    public void saveXmlFromFile(String xml, String remark, String ossPath) throws DocumentException, IOException {
        XmlEntity entity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        if (entity != null) {
            log.info("Start Delete " + xml + " Data From Mysql");
            xmlService.delByRemark(remark);
            log.info("Finish Delete " + xml + " Data From Mysql");
            pointDocMapper.delByKey("xmlUid", entity.getXmlUid());
            objPointsMapper.delByKey("xmlUid", entity.getXmlUid());
            log.info("Finish Delete Data From Es");
        }
        File fileFromOss = getFileFromOss(ossPath);

        String xmlPath = fileFromOss.getAbsolutePath();
        String fileMD5 = Md5Util.getFileMD5(xmlPath);
        ossPath = "null".equals(ossPath) ? null : ossPath;
        String uuid = apolloBaseUtils.getUUID(xml);
        XmlEntity xmlEntity = new XmlEntity();
        xmlEntity.setXmlName(xml);
        xmlEntity.setFileMd5(fileMD5);
        xmlEntity.setOssPath(ossPath);
        xmlEntity.setRemark("null".equals(remark) ? xml : remark);
        xmlEntity.setXmlUid(uuid);
        xmlService.save(xmlEntity);
        log.info("Start read xml: " + xml);
        Document document = saxReader.read(xmlPath);
        log.info("Loaded into memory: " + xml);
        try {
            apolloAttrBuilder.builderAll(document, uuid);
        } catch (IOException e) {
            log.info("Error!!! del data from es");
            pointDocMapper.delByKey("xmlUid", uuid);
            objPointsMapper.delByKey("xmlUid", uuid);
            throw new RuntimeException(e);
        } finally {
            fileFromOss.delete();
        }
    }

}
