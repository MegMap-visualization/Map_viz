package com.megvii.mapapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class OtherLaneInfoEntity extends LaneEntity {
    @TableField(exist = false)
    private String xmlName;

    @TableField(exist = false)
    private String remark;

    @TableField(exist = false)
    private String ossPath;

    @TableField(exist = false)
    private String fileMd5;
}
