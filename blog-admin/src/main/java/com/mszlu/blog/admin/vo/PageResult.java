package com.mszlu.blog.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {//ๅ้กตๆๆ

    private List<T> list;

    private Long total;
}