package com.wikicivi.adapter.holder;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.wikicivi.R;
import com.wikicivi.adapter.holder.base.BaseHolder;

import org.json.JSONException;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/22.
 */
public class AdHolder extends BaseHolder {
    @ViewInject(R.id.adView)
    private AdView adView;

    public AdHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        bindHead(context, cardHolder, jsonStr);
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
