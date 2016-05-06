package com.zky.articleproj.adapter.holder.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.zky.articleproj.R;

import org.json.JSONException;

/**
 * Created by zhaoliang on 16/4/23.
 */
public abstract class BaseHolder extends RecyclerView.ViewHolder implements RecyclerView.OnChildAttachStateChangeListener {

    public Context context;
    private FrameLayout root_layout;

    public BaseHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();
        root_layout = (FrameLayout) itemView.findViewById(R.id.root_layout);
        setView();
    }

    /**
     * 绑定数据到布局文件上
     *
     * @param context
     * @param cardHolder
     * @param jsonStr
     */
    public abstract void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException;

    /**
     * 设置布局文件
     *
     * @return
     */
    public abstract int setLayoutFile();

    public void setView() {
        View view = View.inflate(context, setLayoutFile(), null);
        root_layout.addView(view);
    }

    public void onAttached()
    {


    }

    public void onDetached()
    {


    }
}
