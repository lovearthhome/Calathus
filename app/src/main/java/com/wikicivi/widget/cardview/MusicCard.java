package com.wikicivi.widget.cardview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lovearthstudio.articles.constant.Constant;
import com.zky.articleproj.R;
import com.wikicivi.manager.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.mobiwise.playerview.MusicPlayerView;

/**
 * 音乐的卡片布局
 * Created by zhaoliang on 16/5/12.
 */
public class MusicCard extends BaseCardView implements View.OnClickListener {

    private static final String TAG = "========" + MusicCard.class.getName();

    private TextView tv_music_title;
    private MusicPlayerView mpv;

    private String music_src;
    private String image_src;
    private int music_duration;
    private String title;

    private Player player;

    public MusicCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_music, this, true);
    }


    public void findView() {
        tv_music_title = (TextView) findViewById(R.id.tv_music_title);
        mpv = (MusicPlayerView) findViewById(R.id.mpv);
    }


    public void setOnClickListener() {
        mpv.setOnClickListener(this);
    }

    public void parseData(String jsonStr) throws JSONException {
        Log.i(TAG, jsonStr);

        JSONObject jsonObject = new JSONObject(jsonStr);


        String content_str = jsonObject.optString("content");
        JSONObject content_obj = new JSONObject(content_str);

        String art_brief = content_obj.optString("brief");
        title = content_obj.optString("title");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);

        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);
        music_src = Constant.baseFileUrl + img_file.optString("src");

        music_duration = (int) img_file.optDouble("duration");

        JSONObject file1 = art_files.getJSONObject(1);
        JSONArray image_farray = file1.optJSONArray("farray");
        JSONObject image_file = image_farray.getJSONObject(0);
        image_src = Constant.baseFileUrl + image_file.optString("src");

        tv_music_title.setText(title);
        if (music_duration == 0) music_duration = 100;

        player = new Player(mpv);
        mpv.setCoverDrawable(R.drawable.mycover);


        if (image_src != null) {
            if (mpv.isLoading) {
                System.out.println("RecyclerView------放弃加载!" + title);
            } else {
                System.out.println("RecyclerView------开始加载!" + title);

                mpv.setCoverURL(image_src);
            }
        }
        mpv.setMax(music_duration);
    }

    @Override
    public void onClick(View v) {
        try {
            if (!player.isPrepared() && !player.isPreparing()) {
                player.playUrl(music_src);
                return;
            }

            if (!player.isPrepared() && player.isPreparing()) {
                //FIXME:这个时候界面是不能点击的
                return;
            }

            if (player.isPrepared()) {
                if (player.isPlaying()) {
                    mpv.stop();
                    player.pause();
                } else {
                    mpv.start();
                    player.play();

                }
            }
        } catch (Exception e) {

        }
    }
}
