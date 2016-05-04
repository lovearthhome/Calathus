package com.lovearthstudio.articles.service;

import com.lovearthstudio.articles.net.MyCallBack;

/**
 * Created by zhaoliang on 16/4/30.
 */
public interface ArticleInterface {

    String getData(String channel, String action,long tid, MyCallBack myCallBack);

    void netSuccess(String response);
}
