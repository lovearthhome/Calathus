package com.lovearthstudio.articles.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.lovearthstudio.articles.core.ArtDB;
import com.lovearthstudio.articles.core.Articles;
import com.lovearthstudio.articles.core.MyCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

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
        public String getChannelArticles(String channel, String action, long tid, long rid, final MyCallBack myCallBack) {
            if("Comment".equals(channel) && "load".equals(action))
            {
                JSONArray prevarticles = new JSONArray();

                prevarticles = ArticleHelper.getLocalArticle(rid);//这个地方用rid

                final JSONArray mainArticle = prevarticles;


                ArticleHelper.getArticles("Comment",action,0,tid/**tid as the rid*/,  new MyCallBack() {
                    @Override
                    public void onFailure(JSONObject result) {
                        //fixme: 这块的onfailure是怎么触发到app主模块里面的？这块应该让app主模块的刷新按钮停止旋转。
                        myCallBack.onFailure(result);
                    }

                    @Override
                    public void onResponse(JSONObject result) {
                        try{
                            JSONArray comments = result.optJSONArray("data");
                            JSONArray sumArticles = new JSONArray();
                            if(mainArticle.length() > 0)
                            {
                                sumArticles.put(mainArticle.get(0));
                                if (comments.length() > 0) {
                                    for (int i = 0; i < comments.length(); i++) {
                                        sumArticles.put(comments.get(i));
                                    }
                                }
                            }
                            JSONObject myResult = new JSONObject();
                            myResult.put("data", sumArticles);
                            myCallBack.onResponse(myResult);
                        }catch (Exception e){


                        }
                    }
                });
            }else{ //如果不是comment+load
                ArticleHelper.getArticles(channel,action,tid,rid, myCallBack);
            }


            return null;
        }

        @Override
        public String getDetailedArticles(String channel/**must be Comment*/,String action,long tid, final MyCallBack myCallBack) {

            return null;
        }

        @Override
        public String setArticle(long tid,String from,String which, int param, MyCallBack myCallBack) {
            ArticleHelper.setArticle(tid,from,which,param, myCallBack);
            return null;
        }
        @Override
        public String addArticle(long rid,String cato, String media, int flag,int tmpl, String content, MyCallBack myCallBack) {
            ArticleHelper.addArticle(rid,cato, media,flag , tmpl,content, myCallBack);
            return null;
        }

    }
}
