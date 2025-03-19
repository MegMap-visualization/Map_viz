package com.megvii.mapapi.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName("t_center_lane")
public class CenterLaneEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableField(exist = false)
    private JSONArray points;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属的车道uid
     */
    private String laneUid;
    /**
     * 所属的xml唯一uid
     */
    private String xmlUid;

}
