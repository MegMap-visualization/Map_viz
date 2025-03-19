package com.megvii.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class XmlDto {
//    @TableField(exist = false)
//    private String xmlName;
//
//    @TableField(exist = false)
//    private String remark;
//
//    @TableField(exist = false)
//    private String ossPath;
//
//    @TableField(exist = false)
//    private String fileMd5;

    @TableField(exist = false)
    private JSONArray points;
}
