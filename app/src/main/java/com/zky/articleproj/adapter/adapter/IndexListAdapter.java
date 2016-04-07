package com.zky.articleproj.adapter.adapter;

import android.content.Context;

import com.zky.articleproj.adapter.adapter.base.BaseAdapter;

import org.json.JSONArray;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class IndexListAdapter extends BaseAdapter {

    public IndexListAdapter(Context context, JSONArray jsonArray) {
        super(context, jsonArray);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 4;
    }
}