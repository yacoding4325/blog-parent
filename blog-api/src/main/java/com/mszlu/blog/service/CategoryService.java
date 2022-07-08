package com.mszlu.blog.service;

import com.mszlu.blog.vo.CategoryVo;
import com.mszlu.blog.vo.Result;

public interface CategoryService {//创建一个接口，将文章类别信息提取出来

    CategoryVo findCategoryById(Long categoryId);//查询类别

    Result findAll();//找到所有文章类别

    Result findAllDetail();//找到文章分类


    Result categoryDetailById(Long id);//得到分类文章列表
}
