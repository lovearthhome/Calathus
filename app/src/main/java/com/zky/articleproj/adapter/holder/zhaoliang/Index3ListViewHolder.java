package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.view.View;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;

import org.json.JSONException;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class Index3ListViewHolder extends BaseHolder {

    public Index3ListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {

    }

    @Override
    public int setLayoutFile() {
        return R.layout.index3_list_item;
    }
}
