package com.mszlu.blog.service;

import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;

public interface ArticleService {

    Result listArticle(PageParams pageParams);//分页查询文章列表

    Result hotArticle(int limit);//最热文章

    Result newArticles(int limit);//最新文章

    Result listArchives();//首页的文章归档

    Result findArticleById(Long articleId);//查看文章详情

    Result publish(ArticleParam articleParam);//发布文章服务

}