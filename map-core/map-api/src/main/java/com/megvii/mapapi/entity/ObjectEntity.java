package com.megvii.mapapi.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 
 * @author LingYifeng
 */
@Data
@TableName("t_object")
public class ObjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableField(exist = false)
	private JSONArray points;
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
