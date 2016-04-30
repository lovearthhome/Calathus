package com.lovearthstudio.articles.net;

import com.lovearthstudio.articles.constant.Constant;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class Articles {

    /*
   封装网络部分
    */
    private OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Articles() {
        client = new OkHttpClient();
    }

    /**
     * 请求数据
     */
    public void find(Object param, NetCallBack netCallBack) {
        String requestParams = com.alibaba.fastjson.JSON.toJSONString(param);
        System.out.println("---------request:" + requestParams);
        RequestBody body = RequestBody.create(JSON, requestParams);
        Request request = new Request.Builder()
                .url(Constant.baseUrl)
                .post(body)
                .build();


        client.newCall(request).enqueue(netCallBack);
    }
}
