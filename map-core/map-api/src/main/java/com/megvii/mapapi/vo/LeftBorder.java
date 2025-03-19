package com.megvii.mapapi.vo;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.megvii.mapapi.entity.LaneEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeftBorder extends LaneEntity {

    private JSONArray points;
    /**
     * 自增主键
     */
    private Long id;
    /**
     * lane的唯一标识
     */
    private String uid;

    private String color="#F3F1F1FF";
    private String laneBorderType="broken";
    private String type = "leftBorder";

    public void setUid(String uid) {
        this.uid = uid+":leftBorder";
    }
}
