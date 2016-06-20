package com.lovearthstudio.calathus.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lovearthstudio.calathus.net.HttpOpenHelper;
import com.lovearthstudio.calathus.net.NetCallBack;

import org.xutils.x;

/**
 * Created by pro on 16/3/11.
 */
public class BaseActivity extends AppCompatActivity {
    /*
    网络帮助类
     */
    protected HttpOpenHelper httpOpenHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpOpenHelper = new HttpOpenHelper();
        x.view().inject(this);
    }

    /**
     * 请求数据
     */
    public void post(Object param, NetCallBack netCallBack) {

        httpOpenHelper.post(param, netCallBack);
    }
}
