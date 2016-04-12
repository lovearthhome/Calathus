package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class VideoViewHolder extends BaseHolder {

    @ViewInject(R.id.tv_index1_title)
    private TextView tvTitle;
    @ViewInject(R.id.vv_video)
    private VideoView vv_video;

    private String img_src;

    public VideoViewHolder(View itemView) {
        super(itemView);

    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        VideoViewHolder musicViewHolder = (VideoViewHolder) baseHolder;

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
        img_src = img_file.optString("src");

        vv_video.setMediaController(new MediaController(context));
        vv_video.setVideoURI(Uri.parse(img_src));
    }

    @Override
    public int setLayoutFile() {
        return R.layout.video_view_holder;
    }
}
