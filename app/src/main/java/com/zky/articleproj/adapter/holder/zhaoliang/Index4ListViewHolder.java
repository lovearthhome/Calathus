package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class Index4ListViewHolder extends BaseHolder {

    @ViewInject(R.id.my_button)
    private Button my_button;

    public Index4ListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        my_button.setText(jsonObject.getString("editor_name"));
    }

    @Override
    public int setLayoutFile() {
        return R.layout.index4_list_item;
    }
}
