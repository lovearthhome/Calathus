package com.lovearthstudio.articles.net;

import android.content.Context;
import android.util.Log;

import com.lovearthstudio.articles.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
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
public class Articles implements Callback {

    private IndexRequestParams getArtPrama;
    private Map<String, Object> filters;
    private Context mContext;

    private NetCallBack mNetCallBack;

    private Realm artdb ;
    /*
   封装网络部分
    */
    private OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Articles(Context context) {
        mContext = context;

        client = new OkHttpClient();
        RealmConfiguration config = new RealmConfiguration.Builder(context).setModules(new ArtModule()).build();
        Realm.setDefaultConfiguration(config);


                /*
        初始化请求参数
         */
        getArtPrama = new IndexRequestParams();
        getArtPrama.action = "get_articles";
        // getArtPrama.cato = 100000;
        getArtPrama.order = "inc DESC";
        getArtPrama.fields = new String[]{"inc", "star", "comt", "content", "good", "bad", "shar"};
        getArtPrama.rows = 20;
        getArtPrama.channel = "Recommend";

        getArtPrama.filter = new HashMap<>();
        getArtPrama.filter.put("inc[>]", 0);
    }



    /**
     * 请求数据
     * 只有两种动作：pull,一开始的加载也叫pull
     * push,旧的数据
     */
    public void find(String channel,String action, NetCallBack netCallBack) {
        String local_inc_min_str = mContext.getSharedPreferences("limit", Context.MODE_PRIVATE).getString("inc_min", "0");
        long local_inc_min = Long.parseLong(local_inc_min_str);
        String local_inc_max_str = mContext.getSharedPreferences("limit", Context.MODE_PRIVATE).getString("inc_max", "0");
        long local_inc_max = Long.parseLong(local_inc_max_str);


        getArtPrama.channel = channel;
        //pull
        if(action == "pull")
        {
            getArtPrama.filter.clear();
            getArtPrama.filter.put("inc[>]", local_inc_max_str);
        }

        //push
        if(action == "push")
        {
            getArtPrama.filter.clear();
            getArtPrama.filter.put("inc[<]", local_inc_min_str);
        }


        String requestParams = com.alibaba.fastjson.JSON.toJSONString(getArtPrama);
        System.out.println("---------request:" + requestParams);
        //{"action":"get_articles","channel":"Recommend","dua_id":0,"fields":["inc","star","comt","content","good","bad","shar"],"filter":{"inc[>]":0},"order":"inc DESC","rows":20}




        RequestBody body = RequestBody.create(JSON, requestParams);
        Request request = new Request.Builder()
                .url(Constant.baseUrl)
                .post(body)
                .build();

        mNetCallBack = netCallBack;
        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mNetCallBack.onFailure("fail");
    }

    /**
     * 这里onResponse先截取网络上返回的数据，然后再调用mNetCallBack返回给前段界面
     * */
    @Override
    public void onResponse(Call call, Response response) throws  IOException{
        try {
            String response_body = response.body().string();

            //Constant.binder.netSuccess(response_body);

            Log.e("######", response_body);
            JSONObject jsonResponse = new JSONObject(response_body);
            if (jsonResponse == null) {
                //FIXME: 这个地方，如果出错了，那么就是服务器根本没有返回任何json数据
                Log.e("######", response.body().string());
                return;
            }

            System.out.println("----------response:" + jsonResponse);
            int ret_status = jsonResponse.getInt("status");
            if (ret_status == 100120) {
                //没有数据
                //Nop： 等待最后执行
                mNetCallBack.onResponse(response_body);
                return;
            }
            if (ret_status != 0) {
                //FIXME: 这个地方，如果出错了，没有获得服务器的文章，那么就应该合适的告诉APP.不应该把错误蔓延下去。
                Log.e("######", jsonResponse.toString());
                return;
            }

            JSONObject jsonResult = new JSONObject(jsonResponse.getString("result"));
            JSONArray result = jsonResult.getJSONArray("data");
            String channel = jsonResult.optString("channel");

            //FIXME: 这个地方保存加载更新和加载更多的参数
            long new_inc_min = Long.parseLong(jsonResult.getString("inc_min"));
            long new_inc_max = Long.parseLong(jsonResult.getString("inc_max"));
            //如果服务器返回的数据都是有意义的，不是广告，也有数据
            if(new_inc_max != 0 && new_inc_min != 0)
            {
                //realm要求创建realm对象的语句和获取数据的语句必须在同一个线程
                //所以这个要在这个地方getDefaultInstance
                artdb = Realm.getDefaultInstance();

                /*
                mContext.getSharedPreferences("limit", Context.MODE_PRIVATE)
                        .edit()
                        .putString("inc_max", Long.toString(new_inc_max))
                        .putString("inc_min",  Long.toString(new_inc_min))
                        .commit();
                */
                //第一步：在ArtViewLine表中寻找本地存储是否有匹配的viewlineblock
                RealmResults<ArtViewLine> items = artdb.where(ArtViewLine.class)
                        .equalTo("channel",channel)
                        .greaterThanOrEqualTo("tidmax",new_inc_min)
                        .lessThanOrEqualTo("tidmin",new_inc_max)
                        .findAll();

                //第二步：把新的viewlineblock合并进去


                //第三步：把文章合并入输入表artdata
                for (int i = 0; i < result.length(); i++) {
                    String item = result.get(i).toString();
                    JSONObject jsonItem = new JSONObject(item);
                    int tmpl = jsonItem.getInt("tmpl");
                    //如果是广告的话，因为没有inc字段，所以在下一条解析inc的时候就出错了，这时候就没有调用后面的callback导致app一直在那旋转等待...
                    if(tmpl == 501)
                        continue;
                    long tid = jsonItem.getLong("inc");
                    String data = item;
                    // Transactions give you easy thread-safety

                    artdb.beginTransaction();
                    RealmResults<ArtItem> items = artdb.where(ArtItem.class).equalTo("tid",tid).findAll();
                    if(items.size() > 0)
                    {
                        ArtItem item0 = items.get(0);
                        //item0.setTid(tid);
                        item0.setData(data);
                    }else{

                        //When a query does not have any matches,
                        // the returned RealmResults object will not be null, but the size() method will return 0.
                        ArtItem artItem = artdb.createObject(ArtItem.class);
                        artItem.setTid(tid);
                        artItem.setData(data);
                    }

                    artdb.commitTransaction();
                }

            }


            mNetCallBack.onResponse(response_body);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
