package com.lovearthstudio.articles.constant;

import com.lovearthstudio.articles.service.ArticleService;

/**
 * 常量类
 * 定义项目基本的常量地址
 * Created by pro on 16/2/23.
 */
public class Constant {
    /*用户*/
    public static final String userUrl = "http://api.xdua.org/users";
    /* 项目基本地址 */
    public static final String baseUrl = "http://api.wikicivi.com/articles";
    /* 项目图片基本地址 */
    public static final String baseFileUrl = "http://files.wikicivi.com/files/";
    /* dua_id */
    public static long dua_id = 41;

    /* 屏幕宽高 */
    public static int screenwith;
    public static int screenheight;

    /* 主页面左右边距 */
    public static float mainPadding;
    public static float mainItemPadding;

    /*每次向网络文章请求的文章数目*/
    public static final int artnbArticleCountPerFetch = 20;
    /*每次向数据库文章请求的文章数目*/
    public static final int artdbArticleCountPerFetch = 20;

    public static ArticleService.ArticleBinder binder;
}
