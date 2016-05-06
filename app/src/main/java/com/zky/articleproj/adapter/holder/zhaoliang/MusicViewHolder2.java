package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lovearthstudio.articles.constant.Constant;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.adapter.holder.base.CardHolder;
import com.zky.articleproj.manager.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import co.mobiwise.playerview.MusicPlayerView;


/**
 * Created by zhaoliang on 16/4/7.
 */
public class MusicViewHolder2 extends CardHolder {

    @ViewInject(R.id.tv_index1_title)
    private TextView tvTitle;
    private String music_src;
    private String image_src;
    private int music_duration;
    private String title;

    @ViewInject(R.id.mpv)
    private MusicPlayerView mpv;
    private Player player;

    @Event(R.id.mpv)
    private void click(View view) {
        try {
            if(!player.isPrepared() && !player.isPreparing())
            {
                player.playUrl(music_src);
                return;
            }

            if(!player.isPrepared() && player.isPreparing())
            {
                //FIXME:这个时候界面是不能点击的
                return;
            }

            if(player.isPrepared() )
            {
                if(player.isPlaying())
                {
                    mpv.stop();
                    player.pause();
                }else{
                    mpv.start();
                    player.play();

                }
            }
        } catch (Exception e) {

        }
    }


    public MusicViewHolder2(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        MusicViewHolder2 musicViewHolder = (MusicViewHolder2) cardHolder;

        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);

        JSONObject jsonObject = new JSONObject(jsonStr);
            /*
            内容信息
            */
        // tvTitle.setText(jsonObject.getString("title"));

        String content_str = jsonObject.optString("content");
        JSONObject content_obj = new JSONObject(content_str);

        String art_brief = content_obj.optString("brief");
        title = content_obj.optString("title");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);

        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);
        music_src = Constant.baseFileUrl + img_file.optString("src");

        music_duration = (int)img_file.optDouble("duration");

        JSONObject file1 = art_files.getJSONObject(1);
        JSONArray image_farray = file1.optJSONArray("farray");
        JSONObject image_file = image_farray.getJSONObject(0);
        image_src = Constant.baseFileUrl + image_file.optString("src");


        tvTitle.setText(title);
        if(music_duration == 0) music_duration = 100;

        player = new Player(mpv);
        mpv.setCoverDrawable(R.drawable.mycover);
        if (image_src != null) {
            mpv.setCoverURL(image_src);
        }
        mpv.setCoverURL(image_src);
        mpv.setMax(music_duration);
    }


    @Override
    public int setLayoutFile() {
        return R.layout.music_view_holder2;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
        //Log.i("xxxxx",title+"detached from widow");
//        if(player.isPlaying() || player.isPreparing())
//        {
//            mpv.stop();
//            player.stop();
//            player.reset();
//        }
    }


    @Override
    public void onAttached() {
        super.onAttached();
        Log.i("xxxxx",title+"Attached to widow");
    }

    @Override
    public void onDetached() {
        super.onDetached();
        Log.i("xxxxx",title+"Detached to widow");
    }
}
