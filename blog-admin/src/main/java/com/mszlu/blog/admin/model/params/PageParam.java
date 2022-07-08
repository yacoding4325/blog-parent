package com.mszlu.blog.admin.model.params;

import lombok.Data;

@Data
public class PageParam {

    private Integer currentPage;//当前页数

    private Integer pageSize;//

    private String queryString;//查询条件
}
