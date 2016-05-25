package com.lovearthstudio.articles.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiao on 2016/5/24.
 */
public class ArticleTools {
    public static JSONObject artStr2artJson(String artStr)
    {
        try{
            return new JSONObject(artStr);
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }

}
