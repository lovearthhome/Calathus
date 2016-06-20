package com.lovearthstudio.calathus.widget.cardview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.json.JSONException;

/**
 * 基本的卡片父类,要想实现卡片扩展此类即可
 * Created by zhaoliang on 16/5/12.
 */
public abstract class BaseCardView extends LinearLayout {
    protected  long mTid;

    public BaseCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflaterLayout(context);
        findView();
        setOnClickListener();
    }

    /**
     * 填充布局
     *
     * @param context
     */
    public abstract void inflaterLayout(Context context);

    /**
     * 查找控件
     */
    public abstract void findView();

    /**
     * 设置点击事件
     */
    public abstract void setOnClickListener();

    /**
     * FIXME:这里解析json数据
     *
     * @param jsonStr
     */
    public abstract void parseData(String jsonStr) throws JSONException;
}
