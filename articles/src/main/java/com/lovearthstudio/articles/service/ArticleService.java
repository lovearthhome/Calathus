package com.lovearthstudio.articles.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lovearthstudio.articles.net.ArtDB;
import com.lovearthstudio.articles.net.Articles;
import com.lovearthstudio.articles.net.MyCallBack;

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

    public class ArticleBinder extends Binder implements ArticleInterface {

        @Override
        public String getChannelArticles(String channel, String action, long tid, MyCallBack myCallBack) {
            ArticleHelper.getArticles(channel,action,tid, myCallBack);
            return null;
        }


        @Override
        public String setArticle(long tid,String from,String which, int param, MyCallBack myCallBack) {
            ArticleHelper.setArticle(tid,from,which,param, myCallBack);
            return null;
        }

    }
}
