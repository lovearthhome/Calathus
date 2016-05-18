package com.lovearthstudio.articles.net;

import org.json.JSONObject;

/**
 * Created by zhaoliang on 16/4/6.
 */
public interface MyCallBack {

    void onFailure(JSONObject result);

    void onResponse(JSONObject result);
}
