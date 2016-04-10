package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class Index2ListViewHolder extends BaseHolder {

    private static String[] imageurl = {"file:///android_asset/test/image1.jpg", "file:///android_asset/test/image2.jpg", "file:///android_asset/test/image3.jpg", "file:///android_asset/test/image4.jpg",
            "file:///android_asset/test/image5.jpg", "file:///android_asset/test/image6.jpg", "file:///android_asset/test/image7.jpg", "file:///android_asset/test/image8.jpg"};

    @ViewInject(R.id.tv_index1_title)
    public TextView tvTitle;

    @ViewInject(R.id.iv_gif)
    public ImageView iv_gif;

    private String image_url = "";

    @Event(R.id.iv_gif)
    private void click(View view) {
        Intent intent = new Intent("com.zky.articleproj.activity.ImageZoomActivity");
        intent.putExtra("image_url", image_url);
        context.startActivity(intent);
    }

    public Index2ListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        Index2ListViewHolder holder = (Index2ListViewHolder) baseHolder;
        JSONObject jsonObject = new JSONObject(jsonStr);
            /*
            内容信息
            */
        tvTitle.setText(jsonObject.getString("title"));
        image_url = jsonObject.getString("content");
        Picasso.with(context).load(image_url).into(holder.iv_gif);

    }

    @Override
    public int setLayoutFile() {
        return R.layout.index2_list_item;
    }
}
