package com.megvii.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.megvii.entity.XmlEntity;

import java.util.Set;


/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
public interface XmlService extends IService<XmlEntity> {
    void delByRemark(String remark);

}

