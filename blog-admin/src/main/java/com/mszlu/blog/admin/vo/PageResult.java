package com.mszlu.blog.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {//分页效果

    private List<T> list;

    private Long total;
}