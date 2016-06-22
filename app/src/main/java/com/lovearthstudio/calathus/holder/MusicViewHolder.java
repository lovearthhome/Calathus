package com.lovearthstudio.calathus.holder;

import android.content.Context;
import android.view.View;

import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.widget.cardview.MusicCard;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;


/**
 * Created by zhaoliang on 16/4/7.
 */
public class MusicViewHolder extends CardHolder {

    @ViewInject(R.id.music_card)
    private MusicCard musicCard;

    public MusicViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, JSONObject jsonStr) throws JSONException {
        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);
        musicCard.parseData(jsonStr);
    }


    @Override
    public int setLayoutFile() {
        return R.layout.card_music_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
