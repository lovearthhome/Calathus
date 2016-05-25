package com.lovearthstudio.articles.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class Articles {

    private GetArtParams getArtParams;
    private SetArtParams setArtParams;
    private Context mContext;
    private MyCallBack mMyCallBack;
    private ArtDB artdb;
    private ArtNB artnb;

    public Articles(Context context) {
        mContext = context;

        RealmConfiguration config = new RealmConfiguration.Builder(context).setModules(new ArtModule()).build();
        Realm.setDefaultConfiguration(config);

        artdb = new ArtDB();
        artnb = new ArtNB();
        getArtParams = new GetArtParams();
        setArtParams = new SetArtParams();
        getArtParams.filter = new HashMap<>();
        setArtParams.filter = new HashMap<>();
    }

    /**
     * 请求数据
     * 只有三种动作：
     * load: 一开始的加载
     * pull: 获取新的数据
     * push: 获取旧的数据
     * next: 大于tidref的数据
     */
    public void getArticles(String pchannel, String paction, long ptidref/*参考的tid*/, final MyCallBack myCallBack) {
        try {
            /**
             * 预处理参数
             */
            if(pchannel == "Review")
            {
                //首先,从本地存储中读出本用户已经审阅过的文章的curReviewTid
                long curReviewTid = 0;
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("articles", Activity.MODE_PRIVATE);
                if(sharedPreferences != null)
                {
                    curReviewTid = sharedPreferences.getLong("curReviewTid", 0);
                }
                pchannel = "Review";
                paction = "next";
                ptidref = curReviewTid;
            }
            final String channel = pchannel;
            final String action = paction;
            final long tidref = ptidref;

            Log.i("articles.find", channel + "-" + action + " " + tidref);
            //Fixme: 那么我们要从本地加载数据,加载数据只会加载数据库最新的20条数据
            JSONObject dbresult = new JSONObject();
            JSONArray dbarticles;
            if (action == "load") {
                dbarticles = artdb.loadArticles(channel, 20);
                dbresult.put("data", dbarticles);
                myCallBack.onResponse(dbresult);
                return;
            }

            if (action == "pull") {
                dbarticles = artdb.pullArticles(channel, tidref, 20);
                if (dbarticles.length() != 0) {
                    dbresult.put("data", dbarticles);
                    myCallBack.onResponse(dbresult);
                    return;
                }
            }

            if (action == "push") {
                dbarticles = artdb.pushArticles(channel, tidref, 20);
                if (dbarticles.length() != 0) {
                    dbresult.put("data", dbarticles);
                    myCallBack.onResponse(dbresult);
                    return;
                }
            }

            if (action == "next") {
                dbarticles = artdb.nextArticles(channel, tidref, 1);
                if (dbarticles.length() != 0) {
                    dbresult.put("data", dbarticles);
                    myCallBack.onResponse(dbresult);
                    return;
                }
            }

            mMyCallBack = myCallBack;
            getArtParams.action = "get_articles";
            getArtParams.how = action;
            getArtParams.fields = new String[]{"inc", "star", "comt", "content", "good", "bad", "shar"};
            getArtParams.channel = channel;
            getArtParams.filter = new HashMap<>();
            getArtParams.filter.put("inc[>]", 0);

            if(action == "pull")
            {
                getArtParams.rows = 20;
                getArtParams.filter.clear();
                getArtParams.order = "inc DESC";
                getArtParams.filter.put("inc[>]", tidref);
            }
            if(action == "push")
            {
                getArtParams.rows = 20;
                getArtParams.filter.clear();
                getArtParams.order = "inc DESC";
                getArtParams.filter.put("inc[<]", tidref);
            }
            if(action == "next")
            {
                getArtParams.rows = 20;
                getArtParams.order = "inc ASC";
                getArtParams.filter.clear();
                getArtParams.filter.put("inc[>]", tidref);
            }

            artnb.getArticles(getArtParams,new MyCallBack(){
                @Override
                public void onFailure(JSONObject result) {
                    mMyCallBack.onFailure(result);
                }
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    try {
                        if(jsonResponse == null)
                        {
                            myCallBack.onFailure(null);
                            return;
                        }

                        if(jsonResponse.optInt("status") != 0)
                        {
                            //FIXME: 把错误信息发前端显示
                            myCallBack.onFailure(jsonResponse);
                            return;
                        }
                        //FIXME: 这个地方保存加载更新和加载更多的参数
                        JSONObject echo     = jsonResponse.optJSONObject("echo");
                        String echo_channel = echo.optString("channel");
                        String echo_action  = echo.optString("action");
                        String echo_how     = echo.optString("how");
                        long echo_tidref    = echo.optLong("tidref");


                        JSONObject result   = jsonResponse.optJSONObject("result");
                        JSONArray data      = result.getJSONArray("data");
                        int count           = data.length();
                        long new_inc_min    = result.optLong("inc_min");
                        long new_inc_max    = result.optLong("inc_max");
                        int nomore          = result.optInt("nomore");

                        //如果服务器返回的数据都是有意义的，不是广告，也有数据
                        if(count > 0)
                        {
                            artdb.storeArticles(channel,data,new_inc_max,new_inc_min,nomore);
                            JSONArray myArticles;
                            if(action == "pull"|| action == "push" || action == "load" )
                            {
                                myArticles = artdb.loadArticles(channel,new_inc_min,new_inc_max);
                            }else{
                                myArticles = artdb.nextArticles(channel,tidref,1);
                            }

                            JSONObject myResult = new JSONObject();
                            myResult.put("data",myArticles);
                            myCallBack.onResponse(myResult);
                        }else{
                            JSONObject myResult = new JSONObject();
                            myResult.put("data",new JSONArray());
                            myCallBack.onResponse(myResult);
                        }
                    }catch (JSONException e) {
                        Log.e("Error",e.toString());
                        e.printStackTrace();
                        myCallBack.onResponse(null);
                    }
                }
            });
        }catch (JSONException e) {
                Log.e("Error",e.toString());
                e.printStackTrace();
            }
    }

    /**
     * 获取审核文章
     * 只有三种动作：
     * load: 一开始的加载
     * pull: 获取新的数据
     * push: 获取旧的数据
     */
    public void setArticle(final long tid,final String from, final String which, final int param,final MyCallBack myCallBack) {
        try{
            setArtParams.tid = tid;
            setArtParams.action = "set_article";
            setArtParams.how = from; //"Review"
            setArtParams.field = which; //"good"
            setArtParams.param = param; // 1
            setArtParams.filter.clear();
            setArtParams.filter.put(which,param);


            Log.i("Articles",setArtParams.tid+" "+setArtParams.how);

            artnb.setArticle(setArtParams,new MyCallBack(){
                @Override
                public void onFailure(JSONObject result) {
                    Log.e("Articles","reviewArticle onFailure: "+result.toString());
                    myCallBack.onFailure(result);
                }
                @Override
                public void onResponse(JSONObject jsonResponse) {
                    try {
                        if(jsonResponse.optInt("status") == 0)
                        {
                            if(from == "Review")
                            {
                                //实例化SharedPreferences对象（第一步）
                                SharedPreferences mySharedPreferences= mContext.getSharedPreferences("articles", Activity.MODE_PRIVATE);
                                //实例化SharedPreferences.Editor对象（第二步）
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                //用putString的方法保存数据
                                editor.putLong("curReviewTid", tid);
                                //提交当前数据
                                editor.commit();
                                //使用toast信息提示框提示成功写入数据
                                //Toast.makeText(mContext, "数据成功写入SharedPreferences！" , Toast.LENGTH_LONG).show();
                            }
                            myCallBack.onResponse(jsonResponse);
                        } else{
                            myCallBack.onFailure(jsonResponse);
                        }
                    }catch (Exception e) {
                        Log.e("Error",e.toString());
                        e.printStackTrace();
                        myCallBack.onFailure(null);
                    }
                }
            });
        }catch (Exception e) {
            Log.e("Error",e.toString());
            e.printStackTrace();
        }
    }
}
