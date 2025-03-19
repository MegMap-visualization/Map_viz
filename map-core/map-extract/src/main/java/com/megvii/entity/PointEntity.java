package com.megvii.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@Data
@TableName("t_point")
public class PointEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
//    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String laneUid;
    private Long pIndex;
    private String geo;

    private String xmlUid;
    private BigDecimal lon;
    private BigDecimal lat;
}
