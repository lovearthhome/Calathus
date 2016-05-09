package com.stone.card;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zky.articleproj.R;

/**
 * Author：Mingyu Yi on 2016/5/7 20:39
 * Email：461072496@qq.com
 */
public class MyCardItem extends CardItemView {
    public ImageView imageView;
    private TextView userNameTv;
    private TextView imageNumTv;
    private TextView likeNumTv;

    public MyCardItem(Context context) {
        this(context, null);
    }

    public MyCardItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCardItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.card_item2, this);
        imageView = (ImageView) findViewById(R.id.card_image_view);
        userNameTv = (TextView) findViewById(R.id.card_user_name);
        imageNumTv = (TextView) findViewById(R.id.card_pic_num);
        likeNumTv = (TextView) findViewById(R.id.card_like);
    }

    public void fillData(CardDataItem itemData) {
        ImageLoader.getInstance().displayImage(itemData.imagePath, imageView);
        userNameTv.setText(itemData.userName);
        imageNumTv.setText(itemData.imageNum + "");
        likeNumTv.setText(itemData.likeNum + "");
    }
}
