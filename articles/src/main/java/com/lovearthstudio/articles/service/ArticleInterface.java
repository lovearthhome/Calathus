package com.lovearthstudio.articles.service;

import com.lovearthstudio.articles.net.MyCallBack;

/**
 * Created by zhaoliang on 16/4/30.
 */
public interface ArticleInterface {
    /**
     * 获取某个频道的文章
     * @param channel Recommend/*
     * @param action load/pull/push/next
     * @param tid   文章id
     * @param myCallBack 回调
     * @return
     */
    String getChannelArticles(String channel, String action, long tid, MyCallBack myCallBack);


    /**
     * 审核文章
     * @param from 从哪个渠道设置过来的. Review/View
     * @param tid 审核文章的id
     * @param param 1: 通过  0：不通过
     * @param myCallBack 回调
     * @return
     */
    String setArticle(long tid,String from,String which, int param, MyCallBack myCallBack);

}
