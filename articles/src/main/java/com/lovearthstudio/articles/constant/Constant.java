package com.lovearthstudio.articles.constant;

import android.util.Log;

import com.lovearthstudio.articles.service.ArticleService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 常量类
 * 定义项目基本的常量地址
 * Created by pro on 16/2/23.
 */
public class Constant {
    /*用户*/
    public static final String userUrl = "http://api.xdua.org/users";
    /* 项目基本地址 */
    public static final String baseUrl = "http://api.lovearthstudio.com/articles";
    /* 项目图片基本地址 */
    public static final String baseFileUrl = "http://files.lovearthstudio.com/files/";
    /* dua_id */
    public static long dua_id = 41;

    /* 屏幕宽高 */
    public static int screenwith;
    public static int screenheight;

    /* 主页面左右边距 */
    public static float mainPadding;
    public static float mainItemPadding;

    /*每次向网络文章请求的文章数目*/
    public static final int artnbArticleCountPerFetch = 20;
    /*每次向数据库文章请求的文章数目*/
    public static final int artdbArticleCountPerFetch = 20;

    public static Boolean adflag = true;
    /**距离上次展示广告的文章数目*/
    public static int artsSinceLastAd = 0;
    /**平均多少文章展示一个广告*/
    public static int ArtsPerAd = 10;

    public static ArticleService.ArticleBinder binder;

    public static int NETWORK_FAILURE = 1;
    public static int UNKNOWN_FAILURE = 2;
    public static int JSON_FAILURE    = 3;
    public static int SERVER_FAILURE  = 4;
    public static int NOART_NOTICE    = 5;
    public static int EXCEPTION       = 6;

    /**
     * 把获取的文章Response转换成JSONObject
     */
    public static JSONObject failureObject(int status, String  reason) {
        Log.i("DoOnFailure","fail status: "+status+" "+reason);
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
