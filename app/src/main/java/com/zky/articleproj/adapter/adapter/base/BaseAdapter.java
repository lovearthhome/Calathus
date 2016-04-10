package com.zky.articleproj.adapter.adapter.base;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.adapter.holder.yimingyu.ConversationsListViewHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.Index2ListViewHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.IndexListViewHolder;
import com.zky.articleproj.adapter.holder.zhaoliang.Tmpl102ListViewHolder;

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


    public BaseAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_list_item, parent, false);
        BaseHolder holder = null;
        holder = switchView(viewType, v, holder);
        x.view().inject(holder, v);
        return holder;
    }


    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        try {
            holder.bindBaseView(context, holder, jsonArray.get(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    /**
     * 选择视图
     *
     * @param viewType
     * @param v
     * @param holder
     * @return
     */
    private BaseHolder switchView(int viewType, View v, BaseHolder holder) {
        switch (viewType) {
            case 100:
                holder = new IndexListViewHolder(v);
                break;
            case 102:
                holder = new Tmpl102ListViewHolder(v, sensorManager);
                break;
            case 101:
                holder = new ConversationsListViewHolder(v);
                break;
            case 200:
                holder = new Index2ListViewHolder(v);
                break;
            /*case 1:
                holder = new Index2ListViewHolder(v);
                break;
            case 2:
                holder = new Index3ListViewHolder(v);
                break;
            case 3:
                holder = new Tmpl102ListViewHolder(v);
                break;*/
        }
        return holder;
    }

}
