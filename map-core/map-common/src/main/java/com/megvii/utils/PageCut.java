package com.megvii.utils;


import lombok.Data;

import java.util.Map;

@Data
public class PageCut {
    //每页记录数
    private int pageSize;
    //当前页数
    private int currPage;
    //是否统计总数(默认为true)
    private Boolean searchCount=true;
    //是否分页
    private Boolean isCutPage;
    //拼接的查询条件
    private Map<String, String> queryMap;

}
