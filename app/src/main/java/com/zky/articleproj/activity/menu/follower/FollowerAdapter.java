package com.zky.articleproj.activity.menu.follower;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zky.articleproj.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

/**
 * Author：Mingyu Yi on 2016/4/22 11:04
 * Email：461072496@qq.com
 */
public class FollowerAdapter extends RecyclerView.Adapter<FollowerHolder> {
    public JSONArray jsonArray;
    public FollowerActivity followerActivity;
    public FollowerAdapter(FollowerActivity followerActivity, JSONArray jsonArray) {
        this.followerActivity = followerActivity;
        this.jsonArray = jsonArray;
    }

    @Override
    public FollowerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_view_holder, parent, false);
        FollowerHolder fansHolder = new FollowerHolder(v);
        x.view().inject(fansHolder, v);
        return fansHolder;
    }

    @Override
    public void onBindViewHolder(FollowerHolder holder, int position) {
        try {
            JSONObject jo=jsonArray.getJSONObject(position);
//            holder.fanName.setText(jo.getString("name"));
//            holder.fanSignature.setText(jo.getString("signature"));
//            holder.fanSource.setText(jo.getString("source"));
//
//            String url=jo.getString("avatar");
//            if(!TextUtils.isEmpty(url)){
//                Glide.with(articleActivity).load(url).into(holder.fanAvatar);
//            }
            String buttonText;
            switch (jo.getString("type")){
                case "g1":
                    buttonText="关注";
                    break;
                case "g2":
                    default:
                    buttonText="查看";
            }
            holder.followerAction.setText(buttonText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
}
