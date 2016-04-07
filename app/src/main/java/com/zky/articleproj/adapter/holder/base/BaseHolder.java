package com.zky.articleproj.adapter.holder.base;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zky.articleproj.R;
import com.zky.articleproj.constant.Constant;
import com.zky.articleproj.domain.ArcState;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhaoliang on 16/4/6.
 */
public abstract class BaseHolder extends RecyclerView.ViewHolder {

    private ArcState arcStateParam;
    private List<Map<String, Object>> events;
    private Map<String, Object> event;

    private Context context;

    /**
     * 头部信息
     */
    @ViewInject(R.id.editer_icon)
    public CircleImageView editer_icon;
    @ViewInject(R.id.editor_name)
    public TextView editer_name;

    /**
     * 底部信息
     */
    @ViewInject(R.id.tv_arrow)
    public TextView tv_arrow;
    @ViewInject(R.id.tv_narrow)
    public TextView tv_narrow;
    @ViewInject(R.id.tv_comment)
    public TextView tv_comment;
    @ViewInject(R.id.tv_star)
    public TextView tv_star;
    @ViewInject(R.id.tv_share)
    public TextView tv_share;

    @ViewInject(R.id.tv_arrow_animation)
    private TextView tv_arrow_animation;
    @ViewInject(R.id.tv_narrow_animation)
    private TextView tv_narrow_animation;
    @ViewInject(R.id.tv_star_animation)
    private TextView tv_star_animation;

    private FrameLayout root_layout;
    private JSONObject jsonObject;

    // 点赞+1动画
    public Animation animation;
    public ScaleAnimation scaleAnimation;
    public RotateAnimation rotateAnimation;
    public AnimationSet animationSet;

    public BaseHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();
        root_layout = (FrameLayout) itemView.findViewById(R.id.root_layout);
        setView();

        /*
        动画
         */
        animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.applaud_animation);
        scaleAnimation = new ScaleAnimation(1, 2, 1, 2, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setDuration(1000);
        rotateAnimation = new RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotateAnimation.setDuration(1000);
        animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);

        arcStateParam = new ArcState();
        arcStateParam.dua_id = Constant.dua_id;
        arcStateParam.action = "log_articles";
        event = new HashMap<>();
        events = new ArrayList<>();

    }

    @Event({R.id.tv_arrow, R.id.tv_narrow, R.id.tv_comment, R.id.tv_star, R.id.tv_share})
    private void click(View view) {
        event.clear();
        events.clear();
        try {
            event.put("tid", jsonObject.get("inc"));
            System.out.println(jsonObject.get("inc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        event.put("param", 1);
        event.put("time", 1000);
        TextView textView = (TextView) view;
        textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) + 1));
        int id = view.getId();
        switch (id) {
            case R.id.tv_arrow:
                view.startAnimation(animationSet);
                playAnimation(tv_arrow_animation);
                event.put("act", "good");
                break;
            case R.id.tv_narrow:
                view.startAnimation(animationSet);
                playAnimation(tv_narrow_animation);
                event.put("act", "bad");
                break;
            case R.id.tv_comment:
                event.put("act", "comt");
                break;
            case R.id.tv_star:
                view.startAnimation(animationSet);
                playAnimation(tv_star_animation);
                event.put("act", "star");
                break;
            case R.id.tv_share:
                event.put("act", "shar");
                break;
        }
        events.add(event);
        arcStateParam.events = events;
        //postArtState(arcStateParam);
    }

    public void playAnimation(final View view) {
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                view.setVisibility(View.INVISIBLE);
            }
        }, 1000);
    }

    public void bindBaseView(Context context, BaseHolder baseHolder, String jsonStr) {

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            /**
             * 头部信息
             */
            baseHolder.editer_name.setText(jsonObject.getString("editor_name"));
            Picasso.with(context).load(jsonObject.getString("editor_avatar")).into(baseHolder.editer_icon);

            /**
             * 底部信息
             */
            baseHolder.tv_arrow.setText(jsonObject.getString("good"));
            baseHolder.tv_narrow.setText(jsonObject.getString("bad"));
            baseHolder.tv_comment.setText(jsonObject.getString("comt"));
            baseHolder.tv_star.setText(jsonObject.getString("star"));
            baseHolder.tv_share.setText(jsonObject.getString("shar"));

            bindView(context, baseHolder, jsonStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 绑定数据到布局文件上
     *
     * @param context
     * @param baseHolder
     * @param jsonStr
     */
    public abstract void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException;

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
}
