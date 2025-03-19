package com.megvii.mapapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.megvii.mapapi.entity.XmlEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
public interface XmlService extends BaseService<XmlEntity>{

    Set<String> getXmlList();

}

