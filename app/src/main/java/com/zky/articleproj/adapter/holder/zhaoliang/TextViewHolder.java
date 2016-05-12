package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.view.View;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.adapter.holder.base.CardHolder;
import com.zky.articleproj.view.cardview.TextCard;

import org.json.JSONException;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/6.
 */
public class TextViewHolder extends CardHolder {

    @ViewInject(R.id.text_card)
    private TextCard text_card;

    public TextViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);
        text_card.parseData(jsonStr);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.card_text_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}