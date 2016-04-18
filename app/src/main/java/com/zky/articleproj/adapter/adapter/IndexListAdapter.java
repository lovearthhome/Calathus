package com.zky.articleproj.adapter.adapter;

import android.content.Context;

import com.zky.articleproj.adapter.adapter.base.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class IndexListAdapter extends BaseAdapter {

    public IndexListAdapter(Context context, JSONArray jsonArray) {
        super(context, jsonArray);
    }

    /*这个是baseAdapter的成员函数,专门用来做多种视图的,这个position是内容array的索引
    * recycleView的回收机制就依赖于这个viewtype.保证你需要图片的时候给图片控件,需要视频播放的时候给视频播放控件.
    * */
    @Override
    public int getItemViewType(int position) {
        try {
            JSONObject jsonObject = new JSONObject(jsonArray.get(position).toString());
            return jsonObject.getInt("tmpl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

}