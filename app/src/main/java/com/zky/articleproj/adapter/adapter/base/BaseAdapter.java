package com.zky.articleproj.adapter.adapter.base;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.adapter.holder.yimingyu.ConversationsListViewHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.AdHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.GifPlayerHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.ImageViewHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.IndexListViewHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.MusicViewHolder2;
import com.zky.articleproj.adapter.holder.zhaoliang.VideoViewHolder3;

import org.json.JSONArray;
import org.json.JSONException;
import org.xutils.x;


/**
 * Created by zhaoliang on 16/4/6.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseHolder> {

    public Context context;

    public JSONArray jsonArray;

    SensorManager sensorManager;

    public XRecyclerView listView;

    public BaseAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /*这个函数是XRcycleView触发的,当它发现holder缓冲池里数目<4(在本项目初始)的时候,就会去创建新的viewholder,如果超过了,它就去缓冲池里获取旧的*/
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("RecyclerView", "onCreateViewHolder" + viewType);
        View v = null;
        switch (viewType) {
            case 501:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adindex_list_item, parent, false);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_list_item, parent, false);
                break;
        }
        BaseHolder holder = null;
        switch (viewType) {
            case 100:
                holder = new IndexListViewHolder(v);
                break;
            case 101:
                holder = new ConversationsListViewHolder(v);
                break;
            case 201:
                holder = new ImageViewHolder(v);
                break;
            case 202:
                holder = new GifPlayerHolder(v);
                break;
            case 301:
                holder = new MusicViewHolder2(v);
                break;
            case 401:
                holder = new VideoViewHolder3(v);
                break;
            case 501:
                holder = new AdHolder(v);
                //System.out.println("---------------加载广告!");
                break;
            default:
                //FIXME: 这个地方，如果出错了，没有获得服务器的文章，那么就应该合适的告诉APP.不应该把错误蔓延下去。
                Log.e("######", "收到意料外的view type" + viewType);
        }
        listView.addOnChildAttachStateChangeListener(holder);
        x.view().inject(holder, v);
        return holder;
    }

    /*这个函数是XRcycleView触发的,它自动会把创建的或者回收的holder对象绑定 adapter里jsonarray第position的数据*/
    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        Log.e("RecyclerView", "onBindViewHolder" + position);
        try {
            holder.bindView(context, holder, jsonArray.get(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


}
