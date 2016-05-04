package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.view.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/22.
 */
public class AdHolder extends BaseHolder {

    @ViewInject(R.id.adView)
    private AdView adView;

    /**
     * 头部信息
     */
    @ViewInject(R.id.editer_icon)
    public RoundImageView editer_icon;
    @ViewInject(R.id.editor_name)
    public TextView editer_name;

    public AdHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            /**
             * 获取item需要的基本信息
             *
             * */
            //String editor_name = jsonObject.optString("editor_name");
            /**
             * 头部信息
             */
            editer_name.setText(jsonObject.getString("editor_name"));
            Picasso.with(context).load(jsonObject.getString("editor_avatar")).resize(60, 60).into(editer_icon);

            //   Glide.with(context).load(jsonObject.getString("editor_avatar")).override(100, 100).into(editer_icon);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //System.out.println("---------------加载广告:");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.ad_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
