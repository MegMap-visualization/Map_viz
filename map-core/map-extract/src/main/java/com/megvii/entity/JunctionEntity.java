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
@TableName("t_junction")
public class JunctionEntity extends XmlDto implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private String pointsUid;
    /**
     *
     */
    private String connection;
    /**
     *
     */
    private String xmlUid;


}
