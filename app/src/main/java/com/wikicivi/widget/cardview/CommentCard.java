package com.wikicivi.widget.cardview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lovearthstudio.duasdk.util.LogUtil;
import com.wikicivi.R;
import com.wikicivi.constant.Constant;
import com.wikicivi.widget.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by zhaoliang on 16/6/8.
 */
public class CommentCard extends BaseCardView {
    private Context context;


    public RoundImageView editer_icon;
    public TextView editer_name;
    public TextView tv_comment;
    public CommentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.comment_card, this, true);
    }

    @Override
    public void findView() {
        editer_icon = (RoundImageView) findViewById(R.id.comment_head);
        //editor_name = (TextView) findViewById(R.id.comment_content);
        tv_comment = (TextView) findViewById(R.id.comment_content);
    }

    @Override
    public void setOnClickListener() {

    }

    @Override
    public void parseData(String jsonStr) throws JSONException {
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

            //editer_name.setText("");
            LogUtil.e(avatarUrl);
            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(editer_icon);
            tv_comment.setText("哈哈哈");
            //Picasso.with(context).load(avatar).into(cardHolder.editer_icon);

//            Glide.with(context).load(jsonObject.getString("editor_avatar")).into(cardHolder.editer_icon);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
