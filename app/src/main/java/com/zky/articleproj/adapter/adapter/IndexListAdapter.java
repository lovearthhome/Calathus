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