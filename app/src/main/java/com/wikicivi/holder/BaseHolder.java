package com.wikicivi.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wikicivi.R;
import com.wikicivi.widget.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/23.
 */
public abstract class BaseHolder extends RecyclerView.ViewHolder implements RecyclerView.OnChildAttachStateChangeListener {

    public Context context;
    private FrameLayout root_layout;
    /**
     * 头部信息
     */
    @ViewInject(R.id.editer_icon)
    public RoundImageView editer_icon;
    @ViewInject(R.id.editor_name)
    public TextView editer_name;

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

    public void bindHead(Context context, BaseHolder cardHolder, String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            /**
             * 获取item需要的基本信息
             * */
            //String editor_name = jsonObject.optString("editor_name");
            /**
             * 头部信息
             */
            editer_name.setText(jsonObject.getString("editor_name"));
            Picasso.with(context).load(jsonObject.getString("editor_avatar")).resize(60, 60).into(editer_icon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void onAttached()
//    {
//
//
//    }
//
//    public void onDetached()
//    {
//
//
//    }
}
