package com.stone.card.itemview;

import android.content.Context;
import android.util.AttributeSet;

import com.stone.card.CardDataItem;
import com.stone.card.CardItemView;
import com.zky.articleproj.R;

/**
 * Created by zhaoliang on 16/5/9.
 */
public class MyCardItemView extends CardItemView {
    public MyCardItemView(Context context) {
        super(context);
    }

    public MyCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.card_item2, this);
    }

    @Override
    public void fillData(CardDataItem itemData) {

    }
}
