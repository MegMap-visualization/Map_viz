package com.megvii.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@Data
@TableName("t_signal")
public class SignalEntity extends XmlDto implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     *
     */
    private String signalId;
    /**
     *
     */
    private String type;
    /**
     *
     */
    private String layoutType;
    /**
     * 关联的点序列表
     */
    private String pointsUid;
    /**
     *
     */
    private String stoplineIds;

    private String xmlUid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SignalEntity that = (SignalEntity) o;
        return Objects.equals(signalId, that.signalId) && Objects.equals(xmlUid, that.xmlUid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signalId, xmlUid);
    }
}
