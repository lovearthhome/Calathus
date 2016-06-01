package com.wikicivi.adapter.holder;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.seismic.ShakeDetector;
import com.zky.articleproj.R;
import com.wikicivi.adapter.holder.base.BaseHolder;
import com.wikicivi.adapter.holder.base.CardHolder;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import tyrantgit.explosionfield.ExplosionField;


/**
 * Created by zhaoliang on 16/4/7.
 */
public class Tmpl102ListViewHolder extends CardHolder implements ShakeDetector.Listener {


    @ViewInject(R.id.joke_title)
    private TextView jokeTitle;
    @ViewInject(R.id.joke_ask)
    private TextView jokeAsk;
    @ViewInject(R.id.joke_ans)
    private TextView jokeAns;
    @ViewInject(R.id.joke_why)
    private TextView jokeWhy;

    @ViewInject(R.id.btn_show_ans)
    private Button btn_show_ans;

    @ViewInject(R.id.ans_covering)
    private FrameLayout ans_covering;

    private Animation animation;
    private ExplosionField mExplosionField;

    @Event({R.id.btn_show_ans})
    private void click(View view) {
        //System.out.println("-----------查看答案");
        //ans_covering.startAnimation(animation);
        mExplosionField.explode(ans_covering);
    }

    public Tmpl102ListViewHolder(View itemView, SensorManager sensorManager) {
        super(itemView);

        mExplosionField = ExplosionField.attach2Window((Activity) itemView.getContext());

        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        animation = new RotateAnimation(0, 360);
        animation.setDuration(1000);
        //animation = new TranslateAnimation(0, 0, -200, -200);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {

        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);

        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONObject joke = new JSONObject(jsonObject.getString("content"));

        jokeTitle.setText(jsonObject.getString("title"));
        jokeAsk.setText("问题:" + joke.getString("ask"));

        jokeAns.setText("答案:" + joke.getString("ans"));

        jokeWhy.setText("原因:" + joke.getString("why"));
    }

    @Override
    public int setLayoutFile() {
        return R.layout.index4_list_item;
    }

    @Override
    public void hearShake() {
        mExplosionField.explode(ans_covering);
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
