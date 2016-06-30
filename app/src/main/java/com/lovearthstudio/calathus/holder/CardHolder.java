package com.lovearthstudio.calathus.holder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.lovearthstudio.articles.core.MyCallBack;
import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.activity.CommentActivity;
import com.lovearthstudio.calathus.constant.Constant;
import com.lovearthstudio.calathus.domain.ArcState;
import com.lovearthstudio.calathus.widget.RoundImageView;
import com.lovearthstudio.duasdk.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import razerdp.popup.MorePopup;
import razerdp.popup.ReportPopup;

/**
 * Created by zhaoliang on 16/4/6.
 */
public abstract class CardHolder extends BaseHolder {

    private static final int MOREPOP_CLICK = 1;
    private static final int REPORTPOP_CLICK = 2;
    private ArcState arcStateParam;
    private List<Map<String, Object>> events;
    private Map<String, Object> event;

    private Context mContext;

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
    @ViewInject(R.id.tv_good)
    public TextView tv_good;
    @ViewInject(R.id.tv_bad)
    public TextView tv_bad;
    @ViewInject(R.id.tv_comment)
    public TextView tv_comment;
    @ViewInject(R.id.tv_share)
    public TextView tv_share;

    @ViewInject(R.id.tv_arrow_animation)
    private TextView tv_arrow_animation;
    @ViewInject(R.id.tv_narrow_animation)
    private TextView tv_narrow_animation;

    @ViewInject(R.id.good_button)
    private LikeButton good_button;
    @ViewInject(R.id.bad_button)
    private LikeButton bad_button;


    private JSONObject mJO;

    /**
     * 文章的id
     */
    private long mTid;
    /**
     * 文章是否点赞
     */
    private int mGooded;
    /**
     * 文章是否点踩
     */
    private int mBaded;
    /**
     * 文章是否收藏
     */
    private int mStared;

    public Animation animation;
    public ScaleAnimation scaleAnimation;
    public AnimationSet animationSet;
    /**
     *  点击more按钮弹出的More界面
     *  我们重新揭示了两个按钮的功能
     *  1： like->收藏
     *  2： comment->举报
     * */
    private MorePopup mMorePopup;
    /**
     *  cardhouder自带的举报界面
     *  1： 各种举报理由
     *
     * */
    private ReportPopup reportPopup;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MOREPOP_CLICK:
                    if("collect".equals(msg.obj))
                    {
                        if(mStared > 0){
                            if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                                com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "star", 0, setViewArticleCB);
                            }
                        }else{
                            if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                                com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "star", 1, setViewArticleCB);
                            }
                        }
                    }else if ("report".equals(msg.obj)){
                        reportPopup = new ReportPopup((Activity)context);
                        reportPopup.setClickListener(new ReportPopup.OnClickListener(){
                            @Override
                            public void onItemClick(String which) {
                                Message obtain = Message.obtain();
                                obtain.what = REPORTPOP_CLICK;
                                obtain.obj = which;
                                mHandler.sendMessage(obtain);
                            }
                        });
                        View view = ((Activity) context).findViewById(R.id.iv_more) ;
                        reportPopup.showPopupWindow(view);
                    }
                    break;
                case REPORTPOP_CLICK:
                    if(!"cancel".equals(msg.obj)  && !"report".equals(msg.obj))
                    {
                        /**
                         * 举报，在report里被当作一个和评论一样的级别
                         * 1： rid是文章id
                         * 2:  comment
                         * 3:  举报的类别放在title域
                         */
                        if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                            String jsonStr = "{\"title\":\""+msg.obj+"\",\"brief\":\"\",\"texts\":[],\"files\":[]}";//text的两边必须有双引号，否则在数据库里是: "brief":我是评论
                            com.lovearthstudio.articles.constant.Constant.binder.addArticle(/*rid*/mTid,"Report","Text",101,700     /*举报占的号*/,jsonStr, new reportArticleCallBack());
                        }
                    }else if ("report".equals(msg.obj)){
                        reportPopup = new ReportPopup((Activity)context);
                        reportPopup.setClickListener(new ReportPopup.OnClickListener(){
                            @Override
                            public void onItemClick(String which) {
                                Message obtain = Message.obtain();
                                obtain.what = REPORTPOP_CLICK;
                                obtain.obj = which;
                                mHandler.sendMessage(obtain);
                            }
                        });
                        View view = ((Activity) context).findViewById(R.id.iv_more) ;
                        reportPopup.showPopupWindow(view);
                    }
                    break;
            }
        }
    };

    private setViewArticleCallBack setViewArticleCB;

    public CardHolder(View itemView) {
        super(itemView);
        /*
        动画
         */
        animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.applaud_animation);
        scaleAnimation = new ScaleAnimation(1, 1.5F, 1, 1.5F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setDuration(500);
        animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);

        arcStateParam = new ArcState();
        arcStateParam.dua_id = Constant.dua_id;
        arcStateParam.action = "log_articles";
        event = new HashMap<>();
        events = new ArrayList<>();
        setViewArticleCB = new setViewArticleCallBack();

        mMorePopup = new MorePopup((Activity)context);
        mMorePopup.setClickListener(new MorePopup.OnClickListener(){
            @Override
            public void onItemClick(String which) {
                Message obtain = Message.obtain();
                obtain.what = MOREPOP_CLICK;
                obtain.obj = which;
                mHandler.sendMessage(obtain);
            }
        });
    }

    @Event({R.id.tv_arrow, R.id.tv_narrow, R.id.tv_comment, R.id.tv_share, R.id.iv_more})
    private void click(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.tv_arrow: {
                if (mGooded + mBaded > 0)
                    return;
                mGooded = 1;
                try {
                    mJO.put("good", mJO.optInt("good") + 1);
                    tv_good.setText(mJO.getString("good"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //点击后换颜色
                Drawable drawable = context.getResources().getDrawable(R.mipmap.goodred);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_good.setCompoundDrawables(drawable, null, null, null);
                view.startAnimation(animationSet);
                playAnimation(tv_arrow_animation);
                //event.put("act", "good");
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "good", 1, setViewArticleCB);
                }
                break;
            }

            case R.id.tv_narrow: {
                if (mBaded + mGooded > 0)
                    return;
                mBaded = 1;
                try {
                    mJO.put("bad", mJO.optInt("bad") + 1);
                    tv_bad.setText(mJO.getString("bad"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //点击后换颜色
                Drawable drawable = context.getResources().getDrawable(R.mipmap.badred);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_bad.setCompoundDrawables(drawable, null, null, null);

                view.startAnimation(animationSet);
                playAnimation(tv_narrow_animation);
                //event.put("act", "bad");
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "bad", 1, setViewArticleCB);
                }
                break;

            }

            case R.id.tv_comment:
                //event.put("act", "comt");
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "comt", 1, setViewArticleCB);
                }
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("channel", "Comment");
                intent.putExtra("tid", mTid);
                context.startActivity(intent);
                break;
            case R.id.tv_share:
                //event.put("act", "shar");
                showShare(view.getContext());
                if (com.lovearthstudio.articles.constant.Constant.binder != null) {
                    com.lovearthstudio.articles.constant.Constant.binder.setArticle(mTid, "View", "shar", 1, setViewArticleCB);
                }
                break;
            case R.id.iv_more:
                //System.out.println("---------------HelloMore!");
                mMorePopup.showPopupWindow(view);
                break;
        }
        events.add(event);
        arcStateParam.events = events;
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


    public void bindBaseView(Context context, CardHolder cardHolder, JSONObject jsonObject) {


        setOnLikeListener();

        try {


            /**
             * 获取item需要的基本信息
             * */
            //String editor_name = jsonObject.optString("editor_name");
            mJO = jsonObject;
            mTid = jsonObject.optLong("tid");
            mBaded = jsonObject.optInt("baded");
            mGooded = jsonObject.optInt("gooded");
            mStared = jsonObject.optInt("stared");
            /**
             * 头部信息
             */
            String editorName = jsonObject.getString("ename");
            String avatarUrl = jsonObject.getString("avatar");
            //***回来的Json数据里"editor_name":null,getString这个函数就会把null解释成带双音号的“null”
            if (editorName == null || editorName.equals("null") || TextUtils.isEmpty(editorName)) {
                editorName = "匿名";
            }
            if (avatarUrl == null || avatarUrl.equals("null") || TextUtils.isEmpty(avatarUrl)) {
                avatarUrl = Constant.defaultAvatarUrl;
            }
            cardHolder.editer_name.setText(editorName);
            /**
             * 绘制赞和踩的颜色
             * */
//            Drawable drawable_good;
//            Drawable drawable_bad;
            if (mGooded > 0) {
                //drawable_good = context.getResources().getDrawable(R.mipmap.goodred);
                good_button.setLiked(true);
            } else {
                //drawable_good = context.getResources().getDrawable(R.mipmap.arrow);
                good_button.setLiked(false);
            }
            if (mBaded > 0) {
                //drawable_bad = context.getResources().getDrawable(R.mipmap.badred);
                bad_button.setLiked(true);
            } else {
                bad_button.setLiked(false);
                //drawable_bad = context.getResources().getDrawable(R.mipmap.narrow);
            }
            if(mStared < 1)
                mMorePopup.setStar(true);
            else
                mMorePopup.setStar(false);

            if(mGooded + mBaded >0)
            {
                good_button.setEnabled(false);
                bad_button.setEnabled(false);
            }else{
                good_button.setEnabled(true);
                bad_button.setEnabled(true);
            }

//            /// 这一步必须要做,否则不会显示.
//            drawable_good.setBounds(0, 0, drawable_good.getMinimumWidth(), drawable_good.getMinimumHeight());
//            drawable_bad.setBounds(0, 0, drawable_bad.getMinimumWidth(), drawable_bad.getMinimumHeight());
//            tv_good.setCompoundDrawables(drawable_good, null, null, null);
//            tv_bad.setCompoundDrawables(drawable_bad, null, null, null);


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
            cardHolder.tv_good.setText(jsonObject.getString("good"));
            cardHolder.tv_bad.setText(jsonObject.getString("bad"));
            cardHolder.tv_comment.setText(jsonObject.getString("comt"));
            cardHolder.tv_share.setText(jsonObject.getString("shar"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置 likeListener
     */
    private void setOnLikeListener() {
        good_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (mGooded + mBaded > 0)
                    return;
                mGooded = 1;
                try {
                    mJO.put("good", mJO.optInt("good") + 1);
                    tv_good.setText(mJO.getString("good"));
                    likeButton.setLiked(true);
                    likeButton.setEnabled(false);
                    bad_button.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //fixme：不能取消喜欢
            }
        });

        bad_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (mBaded + mGooded > 0)
                    return;
                mBaded = 1;
                try {
                    mJO.put("bad", mJO.optInt("bad") + 1);
                    tv_bad.setText(mJO.getString("bad"));
                    likeButton.setLiked(true);
                    likeButton.setEnabled(false);
                    good_button.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //fixme：不能取消踩
            }
        });



    }

    /*
    *  从view发射一个飘动的文字
    * */
    private void flyText(View view)
    {
//        final FloatingText  cubicFloatingText = new FloatingText.FloatingTextBuilder()
//                .textColor(Color.RED)
//                .textSize(100)
//                .floatingAnimatorEffect(new CurvePathFloatingAnimator())
//                .floatingPathEffect(new CurveFloatingPathEffect())
//                .textContent("Hello! ").build();
//        cubicFloatingText.attach2Window();
//
//
//        View layoutCurveView = findViewById(R.id.layoutCurveView);
//        final View curveView = findViewById(R.id.curveView);
//        assert curveView != null;
//        assert layoutCurveView != null;
//        layoutCurveView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                cubicFloatingText.startFloating(curveView);
//            }
//        });

    }
    class reportArticleCallBack implements MyCallBack {

        @Override
        public void onFailure(JSONObject reason) {
            System.out.println("举报文章成功" + reason.toString());
        }

        @Override
        public void onResponse(JSONObject result) {
            System.out.println("举报文章失败" + result.toString());
        }
    }

    private void showShare(Context context) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        //分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享的title");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://files.wikicivi.com/files/Videos/Radar/20160614/fec9f62a9f199674f68c7c82de809545.mp4");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("分享的文本");
        oks.setImageUrl("http://files.wikicivi.com/files/Videos/Radar/20160614/fec9f62a9f199674f68c7c82de809545.jpeg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://files.wikicivi.com/files/Videos/Radar/20160614/fec9f62a9f199674f68c7c82de809545.mp4");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        // oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://files.wikicivi.com/files/Videos/Radar/20160614/fec9f62a9f199674f68c7c82de809545.mp4");
        // 启动分享GUI
        oks.show(context);
    }
}
