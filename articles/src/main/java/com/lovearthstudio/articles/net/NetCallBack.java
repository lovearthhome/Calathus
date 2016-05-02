package com.lovearthstudio.articles.net;

/**
 * Created by zhaoliang on 16/4/6.
 */
public interface NetCallBack {

    void onFailure(String reason);

    void onResponse(String result);
}
