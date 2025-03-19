package com.megvii.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName("t_lane")
public class LaneEntity extends XmlDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * lane的唯一标识
     */
    private String uid;
    /**
     * 左侧车道id
     */
    private String leftUid;
    /**
     * 右侧车道id
     */
    private String rightUid;
    /**
     * 后继车道id
     */
    private String suc;
    /**
     * 前驱车道id
     */
    private String pre;
    /**
     * lane的起始坐标lon
     */
    private String startLon;
    /**
     * lane的起始坐标lat
     */
    private String startLat;
    /**
     * lane的终止坐标lon
     */
    private String endLon;
    /**
     * lane的终止坐标lat
     */
    private String endLat;
    /**
     * lane的类型
     */
    private String laneType;
    private String laneBorderType;
    private String speedLimit;
    /**
     * lane的颜色
     */
    private String color;

    private String roadSection;

    private String xmlUid;

    private String laneId;

    private String length;
    private String isVirtual;
    private String signalOverlapIds;
    private String objectOverlapIds;
    private String roadType;
    private String turnType;

}
