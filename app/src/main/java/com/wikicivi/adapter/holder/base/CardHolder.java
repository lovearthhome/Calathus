package com.wikicivi.adapter.holder.base;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lovearthstudio.articles.net.MyCallBack;
import com.lovearthstudio.duasdk.util.LogUtil;
import com.wikicivi.R;
import com.wikicivi.activity.CommentActivity;
import com.wikicivi.activity.MoreActivity;
import com.wikicivi.constant.Constant;
import com.wikicivi.domain.ArcState;
import com.wikicivi.widget.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaoliang on 16/4/6.
 */
public abstract class CardHolder extends BaseHolder {

    private ArcState arcStateParam;
    private List<Map<String, Object>> events;
    private Map<String, Object> event;

    /**
     * 头部信息
     */
    @ViewInject(R.id.editer_icon)
    public RoundImageView editer_icon;
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
    @ViewInject(R.id.tv_share)
    public TextView tv_share;

    @ViewInject(R.id.tv_arrow_animation)
    private TextView tv_arrow_animation;
    @ViewInject(R.id.tv_narrow_animation)
    private TextView tv_narrow_animation;

    private JSONObject jsonObject;

    /**
     * 文章的id
     */
    private long mTid;

    // 点赞+1动画
    public Animation animation;
    public ScaleAnimation scaleAnimation;
    public RotateAnimation rotateAnimation;
    public AnimationSet animationSet;


    private setViewArticleCallBack setViewArticleCB;

    public CardHolder(View itemView) {
        super(itemView);

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
        setViewArticleCB = new setViewArticleCallBack();

    }

    @Event({R.id.tv_arrow, R.id.tv_narrow, R.id.tv_comment, R.id.tv_share, R.id.iv_more})
    private void click(View view) {
        /*event.clear();
        events.clear();
        try {
            event.put("tid", jsonObject.get("inc"));
            //System.out.println(jsonObject.get("inc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        event.put("param", 1);
        event.put("time", 1000);
        TextView textView = (TextView) view;
        textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) + 1));*/
        int id = view.getId();
        switch (id) {
            case R.id.tv_arrow:
                view.startAnimation(animationSet);
                playAnimation(tv_arrow_animation);
                //event.put("act", "good");
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "good", 1, setViewArticleCB);
                }
                break;
            case R.id.tv_narrow:
                view.startAnimation(animationSet);
                playAnimation(tv_narrow_animation);
                //event.put("act", "bad");
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "bad", 1, setViewArticleCB);
                }
                break;
            case R.id.tv_comment:
                //event.put("act", "comt");
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "comt", 1, setViewArticleCB);
                }
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("channel", "Comment");
                intent.putExtra("tid",mTid);
                context.startActivity(intent);
                break;
            case R.id.tv_share:
                //event.put("act", "shar");
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "shar", 1, setViewArticleCB);
                }
                break;
            case R.id.iv_more:
                context.startActivity(new Intent(context, MoreActivity.class));
                break;
        }
        events.add(event);
        arcStateParam.events = events;
        //postArtState(arcStateParam);
    }


    class setViewArticleCallBack implements MyCallBack {

        @Override
        public void onFailure(JSONObject reason) {

        }

        @Override
        public void onResponse(JSONObject result) {

        }
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



    public void bindBaseView(Context context, CardHolder cardHolder, String jsonStr) {

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            /**
             * 获取item需要的基本信息
             * */
            //String editor_name = jsonObject.optString("editor_name");
            mTid = jsonObject.optLong("inc");
            /**
             * 头部信息
             */
            String editorName = jsonObject.getString("editor_name");
            String avatarUrl = jsonObject.getString("editor_avatar");
            //***回来的Json数据里"editor_name":null,getString这个函数就会把null解释成带双音号的“null”
            if (editorName == null || editorName.equals("null") || TextUtils.isEmpty(editorName)) {
                editorName = "匿名";
            }
            if (avatarUrl == null || avatarUrl.equals("null") || TextUtils.isEmpty(avatarUrl)) {
                avatarUrl = Constant.defaultAvatarUrl;
            }
            cardHolder.editer_name.setText(editorName);
            LogUtil.e(avatarUrl);
            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(cardHolder.editer_icon);
            //Picasso.with(context).load(avatar).into(cardHolder.editer_icon);

//            Glide.with(context).load(jsonObject.getString("editor_avatar")).into(cardHolder.editer_icon);

            /**
             * 底部信息
             */
            cardHolder.tv_arrow.setText(jsonObject.getString("good"));
            cardHolder.tv_narrow.setText(jsonObject.getString("bad"));
            cardHolder.tv_comment.setText(jsonObject.getString("comt"));
            cardHolder.tv_share.setText(jsonObject.getString("shar"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
