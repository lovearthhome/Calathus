package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.view.View;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.adapter.holder.base.CardHolder;
import com.zky.articleproj.view.cardview.MusicCard;

import org.json.JSONException;
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
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
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
