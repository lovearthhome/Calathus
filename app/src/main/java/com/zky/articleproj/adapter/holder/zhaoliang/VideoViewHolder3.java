package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.adapter.holder.base.CardHolder;
import com.zky.articleproj.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class VideoViewHolder3 extends CardHolder {

    @ViewInject(R.id.tv_index1_title)
    private TextView tvTitle;

    @ViewInject(R.id.custom_videoplayer_standard)
    private JCVideoPlayerStandard jcVideoPlayerStandard;

    @ViewInject(R.id.video_layout)
    private FrameLayout video_layout;

    private int video_width;
    private int video_height;
    private String video_type;
    private int video_size;
    private int video_duration;

    private boolean needResume; //The variable needResume is used to test whether or not to resume playing video automatically.
    private String video_src;
    private String image_src;

    public VideoViewHolder3(View itemView) {
        super(itemView);

    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {

        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);

        JSONObject jsonObject = new JSONObject(jsonStr);
            /*
            内容信息
            */
        //tvTitle.setText(jsonObject.getString("title"));
        String content_str = jsonObject.getString("content");
        JSONObject content_obj = new JSONObject(content_str);

        tvTitle.setText(content_obj.optString("title"));


        String art_brief = content_obj.optString("brief");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String img_title = file0.optString("title");
        JSONArray video_farray = file0.optJSONArray("farray");
        JSONObject video_file = video_farray.getJSONObject(0);
        video_src = video_file.optString("src");

        JSONObject file1 = art_files.getJSONObject(1);
        JSONArray image_farray = file1.optJSONArray("farray");
        JSONObject image_file = image_farray.getJSONObject(0);
        image_src = image_file.optString("src");


        video_height = video_file.optInt("height");
        video_width = video_file.optInt("width");
        // video_type = video_file.optString("type");
        video_size = video_file.optInt("size");

        // FIXME: 计算图片的宽度和高度,有待优化!
        float ratio = ((float) Constant.screenwith / (float) video_width);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, (int) (ratio * video_height));


        String videoUrl = Constant.baseFileUrl + video_src;
        String imageUrl = Constant.baseFileUrl + image_src;
        Log.e("----------videoUrl:", Constant.baseFileUrl + video_src);
        Log.e("----------imageUrl:", Constant.baseFileUrl + image_src);
        jcVideoPlayerStandard.setUp(videoUrl, content_obj.optString("title"));
        //jcVideoPlayerStandard.setUp("http://2449.vod.myqcloud.com/2449_bfbbfa3cea8f11e5aac3db03cda99974.f20.mp4", content_obj.optString("title"));
        Glide.with(context)
                .load(imageUrl)
                .into(jcVideoPlayerStandard.ivThumb);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.video_view_holder3;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
