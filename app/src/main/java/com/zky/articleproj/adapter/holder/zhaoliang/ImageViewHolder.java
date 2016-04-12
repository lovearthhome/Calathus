package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class ImageViewHolder extends BaseHolder {

    private static String[] imageurl = {"file:///android_asset/test/image1.jpg", "file:///android_asset/test/image2.jpg", "file:///android_asset/test/image3.jpg", "file:///android_asset/test/image4.jpg",
            "file:///android_asset/test/image5.jpg", "file:///android_asset/test/image6.jpg", "file:///android_asset/test/image7.jpg", "file:///android_asset/test/image8.jpg"};

    @ViewInject(R.id.tv_index1_title)
    public TextView tvTitle;

    @ViewInject(R.id.iv_img)
    public ImageView iv_img;

    private String image_url = "";

    @Event(R.id.iv_img)
    private void click(View view) {
        Intent intent = new Intent("com.zky.articleproj.activity.ImageZoomActivity");
        intent.putExtra("image_url", image_url);
        context.startActivity(intent);
    }

    public ImageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        ImageViewHolder holder = (ImageViewHolder) baseHolder;
        JSONObject jsonObject = new JSONObject(jsonStr);
            /*
            内容信息
            */
        tvTitle.setText(jsonObject.getString("title"));

        String content_str = jsonObject.optString("content");
        JSONObject content_obj = new JSONObject(content_str);

        String art_brief = content_obj.optString("brief");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String img_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);
        String img_src = img_file.optString("src");

        image_url = img_src;
        Picasso.with(context).load(image_url).into(holder.iv_img);

    }

    @Override
    public int setLayoutFile() {
        return R.layout.image_view_holder;
    }
}
