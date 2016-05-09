package com.zky.articleproj.activity.menu.followee;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lovearthstudio.duasdk.util.OkHttpUtil;
import com.zky.articleproj.R;
import com.zky.articleproj.base.BaseActivity;
import com.zky.articleproj.constant.Constant;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@ContentView(R.layout.activity_follow)
public class FolloweeActivity extends BaseActivity {
    private static final int LOAD = 1;
    private static final int PULL = 2;
    private static final int PUSH = 3;
    private static final int NO_MORE_DATA = 4;
    private static final int DATA_ERROR = 5;




    @ViewInject(R.id.other_toolbar)
    public Toolbar toolbar;
    @ViewInject(R.id.list_view)
    public XRecyclerView listView;
    public FolloweeAdapter adapter;
    public boolean isPushing;
    public boolean isPulling;
    public FolloweeListRequsetParams params;
    public Gson gson=new Gson();
    public FolloweeCallBack followeeCallBack =new FolloweeCallBack();
    public int fans_last_min=0;
    public int fans_last_max=0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD:
                    adapter.notifyDataSetChanged();
                    break;
                case PULL:
                    adapter.notifyDataSetChanged();
                    listView.refreshComplete();
                    isPulling=false;
                    break;
                case PUSH:
                    adapter.notifyDataSetChanged();
                    listView.loadMoreComplete();
                    isPushing=false;
                    break;
                case DATA_ERROR:
                    Toast.makeText(getApplication(), "错误"+msg.obj, Toast.LENGTH_SHORT).show();
                case NO_MORE_DATA:
                    Toast.makeText(getApplication(), "没有更多", Toast.LENGTH_SHORT).show();
                default:
                    if(isPulling){
                        listView.refreshComplete();
                        isPulling=false;
                    }
                    if(isPushing){
                        listView.loadMoreComplete();
                        isPushing=false;
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setTitle("关注");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        JSONArray ja = new JSONArray();
        adapter = new FolloweeAdapter(this, ja);
        listView.setAdapter(adapter);

        params = new FolloweeListRequsetParams();
        params.dua_id=6;
        params.action = "seekAttention";
        params.who = "Followee";
        params.rows = 10;
        getFollowingList();
        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isPulling=true;
                getFollowingList();
            }

            @Override
            public void onLoadMore() {
                isPushing=true;
                getFollowingList();
            }
        });
    }

    public void getFollowingList(){
        params.filter=new HashMap<>();
        if(isPulling){
            params.filter.put("inc[<]", fans_last_min);
        }else{
            params.filter.put("inc[>]", fans_last_max);
        }
        try{
            Log.e("标志",params.filter.toString());
            OkHttpUtil.asyncPost(Constant.userUrl,gson.toJson(params), followeeCallBack);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public class FolloweeCallBack implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            handler.sendEmptyMessage(DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str=response.body().string();
            Log.e("结果",str);
            try{
                JSONObject jo=new JSONObject(str);
                int status = jo.getInt("status");
                if (status != 0) {
                    Message message=Message.obtain();
                    message.what=DATA_ERROR;
                    message.obj=jo.getString("reason");
                    handler.sendMessage(message);
                    return;
                }
                String string=jo.getString("result");
                if(string.equals("na")){
                    handler.sendEmptyMessage(NO_MORE_DATA);
                    return;
                }
                JSONObject jsonObject = new JSONObject(string);
                fans_last_max=jsonObject.getInt("inc_max");
                fans_last_min=jsonObject.getInt("inc_min");

                JSONArray result = jsonObject.getJSONArray("data");
                if (isPulling) {
                    for (int i = 0; i < adapter.jsonArray.length(); i++) {
                        result.put(adapter.jsonArray.get(i));
                    }
                    adapter.jsonArray = result;
                    handler.sendEmptyMessage(PULL);
                } else if (isPushing) {
                    for (int i = 0; i < result.length(); i++) {
                        adapter.jsonArray.put(result.get(i));
                    }
                    handler.sendEmptyMessage(PUSH);
                } else {
                    for (int i = 0; i < result.length(); i++) {
                        adapter.jsonArray.put(result.get(i));
                    }
                    handler.sendEmptyMessage(LOAD);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    public class FolloweeListRequsetParams {
        public long dua_id;
        public String action;
        public String who;
        public Map<String, Object> filter;
        public int rows;
    }
}
