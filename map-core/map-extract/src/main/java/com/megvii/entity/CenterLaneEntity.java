package com.megvii.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 
 * 
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName("t_center_lane")
public class CenterLaneEntity extends XmlDto implements Serializable {
	private static final long serialVersionUID = 1L;

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
	 * 点的唯一id
	 */
	private String pointsUid;
	/**
	 * 所属的xml唯一uid
	 */
	private String xmlUid;

}
