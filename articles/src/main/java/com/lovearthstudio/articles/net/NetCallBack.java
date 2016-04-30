package com.lovearthstudio.articles.net;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhaoliang on 16/4/6.
 */
public interface NetCallBack extends Callback {

    @Override
    void onFailure(Call call, IOException e);

    @Override
    void onResponse(Call call, Response response) throws IOException;
}
