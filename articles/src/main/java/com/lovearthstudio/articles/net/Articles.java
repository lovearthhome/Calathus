package com.lovearthstudio.articles.net;

import android.content.Context;
import android.util.Log;

import com.lovearthstudio.articles.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class Articles {

    private IndexRequestParams getArtParams;

    private Context mContext;

    private MyCallBack mMyCallBack;

    private ArtDB artdb;
    /*
   封装网络部分
    */
    private OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Articles(Context context) {
        mContext = context;

        client = new OkHttpClient();
        RealmConfiguration config =
                new RealmConfiguration.Builder(context).setModules(new ArtModule()).build();
        Realm.setDefaultConfiguration(config);
        artdb = new ArtDB();

        /*
        初始化请求参数
         */
        getArtParams = new IndexRequestParams();
        getArtParams.action = "get_articles";
        getArtParams.order = "inc DESC";
        getArtParams.fields = new String[]{"inc", "star", "comt", "content", "good", "bad", "shar"};
        getArtParams.rows = 20;
        getArtParams.channel = "Recommend";
        getArtParams.filter = new HashMap<>();
        getArtParams.filter.put("inc[>]", 0);
    }


    public void post(IndexRequestParams param)
    {
        String requestParams = com.alibaba.fastjson.JSON.toJSONString(param);
        //System.out.println("---------request:" + requestParams);
        //{"action":"get_articles","channel":"Recommend","dua_id":0,"fields":["inc","star","comt","content","good","bad","shar"],"filter":{"inc[>]":0},"order":"inc DESC","rows":20}

        RequestBody body = RequestBody.create(JSON, requestParams);
        Request request = new Request.Builder()
                .url(Constant.baseUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mMyCallBack.onFailure("fail");
            }

            /**
             * 这里onResponse先截取网络上返回的数据，然后再调用mNetCallBack返回给前段界面
             * */
            @Override
            public void onResponse(Call call, Response response) throws  IOException{
                try {
                    JSONArray articles = new JSONArray();
                    String response_body = response.body().string();
                    Log.e("wwwResponse", response_body);
                    JSONObject jsonResponse = new JSONObject(response_body);

                    if (jsonResponse == null) {
                        //FIXME: 这个地方，如果出错了，那么就是服务器根本没有返回任何json数据
                        mMyCallBack.onResponse(articles);
                        return;
                    }


                    System.out.println("----------response:" + jsonResponse);

                    int ret_status = jsonResponse.getInt("status");

                    if (ret_status == 100120) {
                        //没有数据
                        //Nop： 等待最后执行
                        mMyCallBack.onResponse(articles);
                        return;
                    }
                    if (ret_status != 0) {
                        //FIXME: 这个地方，如果出错了，没有获得服务器的文章，那么就应该合适的告诉APP.不应该把错误蔓延下去。
                        Log.e("wwwError", jsonResponse.toString());
                        mMyCallBack.onResponse(articles);
                        return;
                    }
                    JSONObject echo = jsonResponse.optJSONObject("echo");
                    String echo_channel = echo.optString("channel");

                    //FIXME: 这个地方保存加载更新和加载更多的参数
                    JSONObject jsonResult = new JSONObject(jsonResponse.getString("result"));
                    JSONArray data = jsonResult.getJSONArray("data");
                    long new_inc_min = Long.parseLong(jsonResult.getString("inc_min"));
                    long new_inc_max = Long.parseLong(jsonResult.getString("inc_max"));
                    int nomore = Integer.parseInt(jsonResult.optString("nomore"));

                    //如果服务器返回的数据都是有意义的，不是广告，也有数据
                    if(new_inc_max != 0 && new_inc_min != 0)
                    {
                        artdb.storeArticles(echo_channel,data,new_inc_max,new_inc_min,nomore);
                        //Log.e("getfromNetwork", data.toString());
                        articles = artdb.loadArticles(echo_channel,new_inc_min,new_inc_max);
                        //Log.e("loadfromLocalDB", articles.toString());
                    }

                    mMyCallBack.onResponse(articles);
                    System.out.println("----------response:" + articles);

                } catch (JSONException e) {
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 请求数据
     * 只有三种动作：
     * load: 一开始的加载
     * pull: 获取新的数据
     * push: 获取旧的数据
     */
    public void find(String channel,String action, long tidref/*参考的tid*/, MyCallBack myCallBack) {
        Log.i("articles.find",channel+"-"+action+" "+tidref);
        mMyCallBack = myCallBack;
        JSONArray dbarticles = new JSONArray();

        //如果action是Load,那么我们要从本地加载数据,加载数据只会加载数据库最新的20条数据
        if(action == "load")
        {
            dbarticles = artdb.loadArticles(channel,20);
            //Fixme： dbarticles mybe empty
            mMyCallBack.onResponse(dbarticles);
        }

        //如果action是pull,那么我们要从本地加载数据,加载数据只会加载比reftid大的紧连着的20篇文章
        if(action == "pull")
        {
            dbarticles = artdb.pullArticles(channel,tidref,20);
            ////System.out.println("@---------pull dbarticles:" + dbarticles);
            if(dbarticles.length() != 0)
            {
                mMyCallBack.onResponse(dbarticles);
            }else{
                getArtParams.channel = channel;
                getArtParams.filter.clear();
                getArtParams.filter.put("inc[>]", tidref);
                post(getArtParams);
            }
        }

        //pull
        if(action == "push")
        {
            dbarticles = artdb.pushArticles(channel,tidref,20);
            if(dbarticles.length() != 0)
            {
                mMyCallBack.onResponse(dbarticles);
            }else{
                getArtParams.channel = channel;
                getArtParams.filter.clear();
                getArtParams.filter.put("inc[<]", tidref);
                post(getArtParams);
            }
        }
    }
}
