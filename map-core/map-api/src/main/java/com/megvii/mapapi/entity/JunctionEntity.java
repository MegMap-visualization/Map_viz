package com.megvii.mapapi.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName("t_junction")
public class JunctionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private JSONArray points;
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String junctionId;
    /**
     *
     */
    private String type;
    /**
     *
     */
    private String connection;
    /**
     *
     */
    private String xmlUid;


}
