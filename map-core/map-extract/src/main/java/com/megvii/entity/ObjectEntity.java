package com.megvii.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 
 * 
 * @author LingYifeng
 */
@Data
@TableName("t_object")
public class ObjectEntity extends XmlDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 
	 */
	private String objectId;
	/**
	 * 
	 */
	private String type;
	/**
	 * 
	 */
	private String pointsUid;

	private String xmlUid;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ObjectEntity that = (ObjectEntity) o;
		return Objects.equals(objectId, that.objectId) && Objects.equals(xmlUid, that.xmlUid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), objectId, xmlUid);
	}
}
