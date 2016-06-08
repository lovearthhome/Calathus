package com.wikicivi.activity.menu.followee;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wikicivi.R;
import com.wikicivi.widget.RoundImageView;

import org.xutils.view.annotation.ViewInject;

/**
 * Author：Mingyu Yi on 2016/4/22 10:46
 * Email：461072496@qq.com
 */
public class FolloweeHolder extends RecyclerView.ViewHolder{
    @ViewInject(R.id.followee_avatar)
    RoundImageView followeeAvatar;
    @ViewInject(R.id.followee_name)
    TextView followeeName;
    @ViewInject(R.id.followee_diary)
    TextView followeeSignature;
    @ViewInject(R.id.followee_action)
    Button followeeAction;

    public FolloweeHolder(View itemView) {
        super(itemView);
    }
}
