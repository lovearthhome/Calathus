package com.zky.articleproj.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.adapter.IndexListAdapter;
import com.zky.articleproj.base.BaseFragment;
import com.zky.articleproj.domain.IndexRequestParams;
import com.zky.articleproj.net.NetCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_index)
public class IndexFragment extends BaseFragment {

    private static final int UPDATE_DATA = 1;
    private static final int PULL = 2;
    private static final int PUSH = 3;
    private static final int PARSE_DATA_ERROR = 4;

    private boolean pull;
    private boolean push;

    private IndexRequestParams getArtPrama;
    private IndexCallBack mIndexCallBack;

    @ViewInject(R.id.list_view)
    private XRecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private IndexListAdapter adapter;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DATA:
                    adapter.notifyDataSetChanged();
                    break;
                case PULL:
                    adapter.notifyDataSetChanged();
                    listView.refreshComplete();
                    break;
                case PUSH:
                    adapter.notifyDataSetChanged();
                    listView.loadMoreComplete();
                    break;
                case PARSE_DATA_ERROR:
                    Toast.makeText(getActivity(), "数据解析错误,请稍后尝试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public IndexFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIndexCallBack = new IndexCallBack();

        /**
         * listview
         */
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setHasFixedSize(true);
        adapter = new IndexListAdapter(getContext(), new JSONArray());
        listView.setAdapter(adapter);

        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pull = true;
                post(getArtPrama, mIndexCallBack);
            }

            @Override
            public void onLoadMore() {
                push = true;
                post(getArtPrama, mIndexCallBack);
            }
        });

        /*
        初始化请求参数
         */
        getArtPrama = new IndexRequestParams();
        getArtPrama.action = "get_articles";
        // getArtPrama.cato = 100000;
        getArtPrama.order = "create_time desc";
        getArtPrama.fields = new String[]{"inc", "brief", "star", "comt", "content", "title", "good", "bad", "shar"};
        getArtPrama.rows = 7;

        Map<String, Object> filters = new HashMap<>();
        //filters.put("media", 1);
        // filters.put("title", "");
        // filters.put("create_time[<]", 0);
        filters.put("cato", 100000);

        getArtPrama.filter = filters;

        post(getArtPrama, mIndexCallBack);
    }

    class IndexCallBack implements NetCallBack {

        @Override
        public void onFailure(Call call, IOException e) {
            mHandler.sendEmptyMessage(PARSE_DATA_ERROR);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            // System.out.println("------------" + response.body().string());
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                System.out.println("----------response:" + jsonObject);
                JSONArray result = jsonObject.getJSONArray("result");
                if (pull) {
                    for (int i = 0; i < adapter.jsonArray.length(); i++) {
                        result.put(adapter.jsonArray.get(i));
                    }
                    adapter.jsonArray = result;
                    pull = false;
                    mHandler.sendEmptyMessage(PULL);
                    System.out.println("----------:pull");
                } else if (push) {
                    for (int i = 0; i < result.length(); i++) {
                        adapter.jsonArray.put(result.get(i));
                    }
                    push = false;
                    mHandler.sendEmptyMessage(PUSH);
                    System.out.println("----------:push");
                } else {
                    for (int i = 0; i < result.length(); i++) {
                        adapter.jsonArray.put(result.get(i));
                    }
                    mHandler.sendEmptyMessage(UPDATE_DATA);
                    System.out.println("-----------:load");
                }

                System.out.println("-----------:" + adapter.jsonArray);
                System.out.println("-----------:" + adapter.jsonArray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求数据
     *//*
    private void postArtState(Object param) {
        String requestParams = com.alibaba.fastjson.JSON.toJSONString(param);
        System.out.println("---------" + requestParams);
        RequestBody body = RequestBody.create(JSON, requestParams);
        Request request = new Request.Builder()
                .url(Constant.baseUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                mHandler.sendEmptyMessage(PARSE_DATA_ERROR);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                System.out.println("------------" + response.body().string());
            }
        });
    }*/
}
