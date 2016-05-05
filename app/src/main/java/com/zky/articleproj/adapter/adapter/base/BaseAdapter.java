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
    /*

        http://www.68idc.cn/help/mobilesys/android/20160418610015.html
    * Called when a view created by this adapter has been detached from its window.         *         * <p>Becoming detached from the window is not necessarily a permanent condition;         * the consumer of an Adapter's views may choose to cache views offscreen while they         * are not visible, attaching an detaching them as appropriate.</p>         *         * @param holder Holder of the view being detached
    * public void onViewDetachedFromWindow(VH holder) {        }
    Called when a view created by this adapter has been detached from its window.
    （当适配器创建的view（即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用）
    *
    *这个方法就是用来当你的列表项滑出可见窗口之外的时候，需要重写此方法进行相应的一些操作。
    *
    *
    * 这个方法具体什么时候用呢？

比如：
我有一个列表，列表的每一个列表项里面都要播放一个短视频，这时候，当我滑动一个列表项直至它消失在可视界面时，便会调用onViewDetachedFromWindow()方法，重要的一点，视频控件也会执行它自己的onViewDetachedFromWindow()方法，那么此时我再滑动回来，让该列表项出现在当前界面，会发现视频那一部分就是黑屏或者白屏了。
注意，出现这个Bug的条件是，该列表项滑动出可视界面，但是滑动距离不长，因为长的话，你再滑回来就会复用View执行onBindViewHolder()方法。
解决方法就是在RecyclerView中重写onViewDetachedFromWindow()方法，对视频进行一个相应的操作（初始化等等）。
    *
    * */

    @Override
    public void onViewAttachedToWindow(BaseHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


}
