package com.lovearthstudio.calathus.widget.cardview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 视频的卡片布局
 * Created by zhaoliang on 16/5/12.
 */
public class VideoCard extends BaseCardView {

    private static final String TAG = "========" + VideoCard.class.getName();

    //    控件相关
    private TextView tv_title;
    private TextView tv_brief;
    private LinearLayout layout_titleframe;
    private LinearLayout layout_briefframe;
    private FrameLayout video_layout;
    private JCVideoPlayerStandard video_view;
    private Context context;

    //     视频相关
    private int video_width;
    private int video_height;
    private String video_type;
    private int video_size;
    private int video_duration;

    private boolean needResume; //The variable needResume is used to test whether or not to resume playing video automatically.
    private String video_src;
    private String image_src;

    public VideoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_video, this, true);
    }

    @Override
    public void findView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_brief = (TextView) findViewById(R.id.tv_brief);
        layout_titleframe = (LinearLayout) findViewById(R.id.layout_titleframe);
        layout_briefframe = (LinearLayout) findViewById(R.id.layout_briefframe);
        video_layout = (FrameLayout) findViewById(R.id.video_layout);
        video_view = (JCVideoPlayerStandard) findViewById(R.id.video_view);
    }

    @Override
    public void setOnClickListener() {

    }

    @Override
    public void parseData(String jsonStr) throws JSONException {
        Log.i(TAG, jsonStr);

        JSONObject jsonObject = new JSONObject(jsonStr);
            /*
            内容信息
            */
        String content_str = jsonObject.optString("content");
        JSONObject content_obj = new JSONObject(content_str);

        String title = content_obj.getString("title");
        if(title == null || title.equals("")  )
        {
            layout_titleframe.setVisibility(View.GONE);
        }else{
            tv_title.setText(title);
        }

        String brief = content_obj.getString("brief");
        if(brief == null || brief.equals("")  )
        {
            layout_titleframe.setVisibility(View.GONE);
        }else{
            tv_brief.setText(brief);
        }
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String img_title = file0.optString("title");
        JSONArray video_farray = file0.optJSONArray("farray");
        JSONObject video_file = video_farray.getJSONObject(0);
        video_src = video_file.optString("src");

        //早期，有些图片没有缩略图
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
        video_view.setUp(videoUrl, content_obj.optString("title"));
        //jcVideoPlayerStandard.setUp("http://2449.vod.myqcloud.com/2449_bfbbfa3cea8f11e5aac3db03cda99974.f20.mp4", content_obj.optString("title"));
        Glide.with(context)
                .load(imageUrl)
                .into(video_view.ivThumb);
    }
}
