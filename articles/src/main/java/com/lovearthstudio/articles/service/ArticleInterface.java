package com.lovearthstudio.articles.service;

import com.lovearthstudio.articles.net.NetCallBack;

/**
 * Created by zhaoliang on 16/4/30.
 */
public interface ArticleInterface {

    String getData(String channel, String action, NetCallBack netCallBack);

    void netSuccess(String response);
}
