package com.mszlu.blog.dao.pojo;

import lombok.Data;

@Data
public class ArticleTag {//关联表的对象

    private Long id;

    private Long articleId;

    private Long tagId;

}
