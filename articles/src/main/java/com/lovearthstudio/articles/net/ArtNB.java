package com.lovearthstudio.articles.net;

import android.util.Log;

import com.lovearthstudio.articles.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
public class ArtNB {
    /*
    * 封装网络部分
    */
    private OkHttpClient httpClient;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static int NETWORK_FAILURE = 1;
    public static int SERVER_FAILURE  = 2;
    public static int SERVER_NOARTICLE = 3;

    public ArtNB() {
        httpClient = new OkHttpClient();
    }


    /**
     * 对tid的文章设置各个域
     */
    public void setArticle(SetArtParams setArtParams,final  MyCallBack myCallBack) {

        Log.i("setArticle","begin");
        String requestParams = com.alibaba.fastjson.JSON.toJSONString(setArtParams);
        System.out.println("---------request:" + requestParams);
        //{"action":"get_articles","channel":"Recommend","dua_id":0,"fields":["inc","star","comt","content","good","bad","shar"],"filter":{"inc[>]":0},"order":"inc DESC","rows":20}
        RequestBody body = RequestBody.create(JSON, requestParams);
        Request request = new Request.Builder().url(Constant.baseUrl).post(body).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myCallBack.onFailure(doOnFailure(NETWORK_FAILURE,e.toString()));
            }
            @Override
            public void onResponse(Call call, Response response) throws  IOException{
                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    //FIXME: 这个地方，如果出错了，那么就是服务器数据链路出错或者服务器返回500等错误
                    if (jsonResponse == null) {
                        myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail to transfer response body to JSONObject"));
                        return;
                    }
                    int ret_status = jsonResponse.optInt("status");
                    //FIXME: 这个地方，如果出错了，没有获得服务器的文章，那么就应该合适的告诉APP.不应该把错误蔓延下去。
                    if (ret_status !=  0) {
                        myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with error code:"+ ret_status));
                        return;
                    }
                    myCallBack.onResponse(jsonResponse);

                } catch (JSONException e) {
                    myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with JSONExecption:" + e.toString()));
                    return;
                }catch (IOException e){
                    myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with IOExecption: "+e.toString()));
                    return;
                }catch (Exception e){
                    myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with Execption: "+e.toString()));
                    return;
                }
            }
        });
    }


    /**
     * 获取审核文章
     */

    public void getArticles(GetArtParams getArtParams, final MyCallBack myCallBack) {
        String requestParams = com.alibaba.fastjson.JSON.toJSONString(getArtParams);
        Log.i("Articles-ArtNB","getArticles "+requestParams);
        //System.out.println("---------request:" + requestParams);
        //{"action":"get_articles","channel":"Recommend","dua_id":0,"fields":["inc","star","comt","content","good","bad","shar"],"filter":{"inc[>]":0},"order":"inc DESC","rows":20}
        RequestBody body = RequestBody.create(JSON, requestParams);
        Request request = new Request.Builder().url(Constant.baseUrl).post(body).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myCallBack.onFailure(doOnFailure(SERVER_FAILURE,e.toString()));
            }
            @Override
            public void onResponse(Call call, Response response) throws  IOException{
                try {
                    String response_body_string = response.body().string();
                    Log.i("ArtNB",response_body_string);
                    //FIXME: 这个地方，如果出错了，那么就是服务器数据链路出错或者服务器返回500等错误
                    //FIXME: 这个地方，有可能是网管服务器给的错误，比如，计算所。返回：﻿﻿<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"开头的计算所无线网络portal认证页面
                    //fixme: 这种情况下，jsonResponse不确定是谁，直接跳到catch JSONException这个语句.
                    JSONObject jsonResponse = new JSONObject(response_body_string);

                    if (jsonResponse == null) {
                        myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail to transfer response body to JSONObject"));
                        return;
                    }
                    int ret_status = jsonResponse.optInt("status");
                    //FIXME: 100120=没有数据
                    if (ret_status == 100120) {
                        myCallBack.onFailure(doOnFailure(SERVER_NOARTICLE,"No article found in server!"));
                        return;
                    }
                    //FIXME: 这个地方，如果出错了，没有获得服务器的文章，那么就应该合适的告诉APP.不应该把错误蔓延下去。
                    if (ret_status !=  0) {
                        myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with error code:"+ ret_status));
                        return;
                    }
                    myCallBack.onResponse(jsonResponse);
                    return;
                } catch (JSONException e) {
                    Log.e("Error",e.toString());
                    e.printStackTrace();
                    myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with JSONExecption: "+e.toString()));
                    return;
                } catch (IOException e){
                    myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with IOExecption: "+e.toString()));
                    return;
                }catch (Exception e){
                    myCallBack.onFailure(doOnFailure(SERVER_FAILURE,"Fail with Execption: "+e.toString()));
                    return;
                }
            }
        });
    }

    /**
     * 把获取的文章Response转换成JSONObject
     */
    private JSONObject doOnFailure(int status,String  reason) {
        try{
            JSONObject result = new JSONObject();
            result.put("status",status);
            result.put("reason",reason);
            return result;
        }catch (JSONException e) {
            Log.e("Error",e.toString());
            e.printStackTrace();
            return null;
        }
    }


}




