package com.wikicivi.adapter.holder;

import android.content.Context;
import android.view.View;

import com.zky.articleproj.R;
import com.wikicivi.adapter.holder.base.BaseHolder;
import com.wikicivi.adapter.holder.base.CardHolder;
import com.wikicivi.widget.cardview.VideoCard;

import org.json.JSONException;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class VideoViewHolder extends CardHolder {

    @ViewInject(R.id.video_card)
    private VideoCard video_card;

    public VideoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);
        video_card.parseData(jsonStr);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.card_video_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}