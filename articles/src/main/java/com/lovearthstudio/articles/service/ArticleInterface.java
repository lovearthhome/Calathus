package com.lovearthstudio.articles.service;

import com.lovearthstudio.articles.core.MyCallBack;

/**
 * Created by zhaoliang on 16/4/30.
 */
public interface ArticleInterface {
    /**
     * 获取某个频道的文章,这个函数也解释抽象频道，如Review/Comment
     * @param channel Recommend/Review/Comment
     * @param action load/pull/push/next
     * @param tid   文章tid
     * @param tid   文章rid
     * @param myCallBack 回调
     * @return
     */
    String getChannelArticles(String channel, String action, long tid, long rid, MyCallBack myCallBack);
    /**
     * 获取某个频道的文章
     * @param channel Comment/*
     * @param action load/pull/push/next
     * @param tid   文章tid
     * @param myCallBack 回调
     * @return
     */
    String getDetailedArticles(String channel/**must be Comment*/,String action,long tid, final MyCallBack myCallBack);
    /**
     * 审核文章
     * @param from 从哪个渠道设置过来的. Review/View
     * @param tid 审核文章的id
     * @param param 1: 通过  0：不通过
     * @param myCallBack 回调
     * @return
     */
    String setArticle(long tid,String from,String which, int param, MyCallBack myCallBack);


    /**
     * 添加文章，在这个articles里要获取
     * @param from 从哪个渠道添加的的. 因为添加的地方是安卓系统app，所以就通用添加为com.lovearthstudio.calathus
     * @param rid 文章的关联id
     * @param cato 文章类别
     * @param media 文章的媒体类型： Text/Image/Video/Audio
     * @param flag  要求强制覆盖的flag
     * @param tmpl  文章的模板类型
     * @param content  文章的主要类型
     * @param myCallBack 回调
     * @return
     */
    String addArticle(long rid,String cato, String media, int flag,int tmpl, String content, MyCallBack myCallBack);



}
