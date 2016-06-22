package com.lovearthstudio.calathus.holder;

import android.content.Context;
import android.view.View;

import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.widget.cardview.ImageCard;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class ImageViewHolder extends CardHolder {

    @ViewInject(R.id.image_card)
    private ImageCard image_card;

    public ImageViewHolder(View itemView) {
        super(itemView);
    }

    /*这个bindView实质是android的recycleview触发的,这里的baseholer实质是指向各个子类的基类引用,
    * 这个baseholder有可能是刚创建的
    * 有可能是回收的.如果是回收的,上面的视频啊,图片啊,gif啊,还在,并且还可以播放.
    * 为了避免这种情况,我们需要对holder进行清理.
    * */
    @Override
    public void bindView(final Context context, BaseHolder cardHolder, JSONObject jsonStr) throws JSONException {
        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);
        image_card.parseData(jsonStr);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.card_image_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
