package com.lovearthstudio.calathus.holder;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;


import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.widget.cardview.TextsCard;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

/**
 * Author：Mingyu Yi on 2016/4/8 09:42
 * Email：461072496@qq.com
 */
public class TextsViewHolder extends CardHolder {

    @ViewInject(R.id.texts_card)
    private TextsCard texts_card;

    public TextsViewHolder(View itemView) {
        super(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void bindView(Context context, BaseHolder cardHolder, JSONObject jsonStr) throws JSONException {
        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);
        texts_card.parseData(jsonStr);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.card_texts_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
