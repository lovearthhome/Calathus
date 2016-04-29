package com.zky.articleproj.activity.menu.follower;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zky.articleproj.R;
import com.zky.articleproj.view.RoundImageView;

import org.xutils.view.annotation.ViewInject;

/**
 * Author：Mingyu Yi on 2016/4/22 10:46
 * Email：461072496@qq.com
 */
public class FollowerHolder extends RecyclerView.ViewHolder{
    @ViewInject(R.id.follower_avatar)
    RoundImageView followerAvatar;
    @ViewInject(R.id.follower_name)
    TextView followerName;
    @ViewInject(R.id.follower_signature)
    TextView followerSignature;
    @ViewInject(R.id.follower_source)
    TextView followerSource;
    @ViewInject(R.id.follower_action)
    Button followerAction;

    public FollowerHolder(View itemView) {
        super(itemView);
    }
}
