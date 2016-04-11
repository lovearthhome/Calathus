package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class IndexListViewHolder extends BaseHolder {


    @ViewInject(R.id.tv_index1_title)
    public TextView tvTitle;
    @ViewInject(R.id.tv_index1_content)
    public TextView tvContent;

    public IndexListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        IndexListViewHolder holder = (IndexListViewHolder) baseHolder;
        JSONObject jsonObject = new JSONObject(jsonStr);
        String title = jsonObject.getString("title");
        System.out.println("--------:" + title);
        if ("null".equals(title) || TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);

            System.out.println("--------设置不显示");
        } else {
            tvTitle.setText(title);
        }
            /*
            内容信息
            */
        holder.tvContent.setText(Html.fromHtml(jsonObject.getString("content")));

    }

    @Override
    public int setLayoutFile() {
        return R.layout.index1_list_item;
    }
}