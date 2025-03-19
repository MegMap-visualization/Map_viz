package com.megvii.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@Data
@TableName("t_xml")
public class XmlEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * xml名称
     */
    private String xmlName;
    /**
     * 备注名称
     */
    private String remark;
    /**
     * ossPath
     */
    private String ossPath;
    /**
     * xmlUid标识
     */
    private String xmlUid;
    private String fileMd5;

}
