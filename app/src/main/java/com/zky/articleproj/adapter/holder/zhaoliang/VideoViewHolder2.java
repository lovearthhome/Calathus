package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

import io.vov.vitamio.utils.Log;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class VideoViewHolder2 extends BaseHolder {

    @ViewInject(R.id.tv_index1_title)
    private TextView tvTitle;
    @ViewInject(R.id.videoView)
    private UniversalVideoView mVideoView;
    @ViewInject(R.id.media_controller)
    private UniversalMediaController mMediaController;
    @ViewInject(R.id.video_layout)
    private FrameLayout video_layout;

    private int img_width;
    private int img_height;
    private String img_type;
    private int img_size;
    private int img_duration;

    private boolean needResume; //The variable needResume is used to test whether or not to resume playing video automatically.
    private String img_src;

    public VideoViewHolder2(View itemView) {
        super(itemView);

    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        VideoViewHolder2 musicViewHolder = (VideoViewHolder2) baseHolder;

        JSONObject jsonObject = new JSONObject(jsonStr);
            /*
            内容信息
            */
        //tvTitle.setText(jsonObject.getString("title"));
        String content_str =  jsonObject.getString("content");
        JSONObject content_obj=new JSONObject(content_str);

        tvTitle.setText(content_obj.optString("title"));


        String art_brief = content_obj.optString("brief");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String img_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);
        img_src = img_file.optString("src");

        img_height = img_file.optInt("height");
        img_width = img_file.optInt("width");
        // img_type = img_file.optString("type");
        img_size = img_file.optInt("size");

        // FIXME: 计算图片的宽度和高度,有待优化!
        float ratio = ((float) Constant.screenwith / (float) img_width);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, (int) (ratio * img_height));

        mVideoView.setLayoutParams(layoutParams);


        String videoUrl = Constant.baseFileUrl + img_src;
        Log.e("----------videoUrl:", Constant.baseFileUrl + img_src);
        mVideoView.setVideoURI(Uri.parse(videoUrl));
        mVideoView.setMediaController(mMediaController);

        System.out.println("--------" + img_src);

        mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {

            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) { // Video pause
            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) { // Video start/resume to play
            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading
            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading
            }

        });

    }

    @Override
    public int setLayoutFile() {
        return R.layout.video_view_holder2;
    }
}
