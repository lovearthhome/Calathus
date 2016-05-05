package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
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

    @ViewInject(R.id.mpv)
    private MusicPlayerView mpv;
    private Player player;

    @Event(R.id.mpv)
    private void click(View view) {
        try {
            if (mpv.isRotating()) {
                mpv.stop();
                player.stop();
            } else {
                mpv.start();
                player.play();
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
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String img_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);
        music_src = Constant.baseFileUrl + img_file.optString("src");

        JSONObject file1 = art_files.getJSONObject(1);
        JSONArray image_farray = file1.optJSONArray("farray");
        JSONObject image_file = image_farray.getJSONObject(0);
        image_src = Constant.baseFileUrl + image_file.optString("src");

        music_duration = 100;

        player = new Player();

        if (music_src != null) {
            player.playUrl(music_src);

            System.out.println("music_src:" + music_src);

            //mpv.setCoverURL("https://upload.wikimedia.org/wikipedia/en/b/b3/MichaelsNumberOnes.JPG");
            mpv.setCoverURL(image_src);
            mpv.setMax(music_duration);
        }

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

    }
}
