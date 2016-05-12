package com.zky.articleproj.adapter.holder.yimingyu;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.adapter.holder.base.CardHolder;
import com.zky.articleproj.view.cardview.TextsCard;

import org.json.JSONException;
import org.xutils.view.annotation.ViewInject;

/**
 * Author：Mingyu Yi on 2016/4/8 09:42
 * Email：461072496@qq.com
 */
public class ConversationsListViewHolder extends CardHolder {

    @ViewInject(R.id.texts_card)
    private TextsCard texts_card;

    public ConversationsListViewHolder(View itemView) {
        super(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
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
