package com.megvii.mapapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
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
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String pointsUid;
    /**
     * 点序列
     */
    private String points;

    private String xmlUid;
}
