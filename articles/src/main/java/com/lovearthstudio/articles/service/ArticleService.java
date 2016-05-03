package com.lovearthstudio.articles.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lovearthstudio.articles.net.ArtDB;
import com.lovearthstudio.articles.net.Articles;
import com.lovearthstudio.articles.net.NetCallBack;

public class ArticleService extends Service {
    private static final String TAG = ArticleService.class.getName();

    private Articles ArticleHelper;

    private ArtDB artDB ;

    public ArticleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ArticleBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建article辅助对象
        ArticleHelper = new Articles(this);
        //创建一个lrucache

        Log.i(TAG, "remoteOncreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "remoteOndestory");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public String getData() {
        System.out.println("------------Service getData");
        return "haha";
    }

    public class ArticleBinder extends Binder implements ArticleInterface {


        @Override
        public String getData(String channel, String action,long tid, NetCallBack netCallBack) {
            ArticleHelper.find(channel,action,tid, netCallBack);
            return null;
        }

        @Override
        public void netSuccess(String response) {
            System.out.println("--------------远程收到数据:" + response);
        }
    }
}
