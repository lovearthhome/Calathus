package com.lovearthstudio.calathus.holder;

import android.content.Context;
import android.view.View;

import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.widget.cardview.CommentCard;

import org.json.JSONException;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/6/8.
 */
public class CommentViewHolder extends BaseHolder {

    @ViewInject(R.id.comment_card)
    private CommentCard commentCard;

    public CommentViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        bindHead(context, cardHolder, jsonStr);
        commentCard.parseData(jsonStr);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.comment_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
