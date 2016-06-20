package com.lovearthstudio.calathus.holder;

import android.content.Context;
import android.view.View;

import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.widget.cardview.GifCard;

import org.json.JSONException;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class GifPlayerHolder extends CardHolder {

    @ViewInject(R.id.gif_card)
    private GifCard gifCard;

    public GifPlayerHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);

        gifCard.parseData(jsonStr);
    }


    @Override
    public int setLayoutFile() {
        return R.layout.card_gif_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }

}
