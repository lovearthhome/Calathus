package com.wikicivi.adapter.holder;

import android.content.Context;
import android.view.View;

import com.wikicivi.R;
import com.wikicivi.adapter.holder.base.BaseHolder;
import com.wikicivi.adapter.holder.base.CardHolder;
import com.wikicivi.widget.cardview.MusicCard;

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
