package com.wikicivi.widget.cardview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.wikicivi.R;

import org.json.JSONException;

/**
 * Created by zhaoliang on 16/6/8.
 */
public class CommentCard extends BaseCardView {

    public CommentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.comment_card, this, true);
    }

    @Override
    public void findView() {

    }

    @Override
    public void setOnClickListener() {

    }

    @Override
    public void parseData(String jsonStr) throws JSONException {

    }
}
