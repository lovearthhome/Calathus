package com.zky.articleproj.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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
    private static final int PULL_TOP = 5;

    private boolean pull;
    private boolean push;

    private IndexRequestParams getArtPrama;
    private IndexCallBack mIndexCallBack;

    @ViewInject(R.id.list_view)
    private XRecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private IndexListAdapter adapter;

    private String channel = "";
    private int tmpl = 200;

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
                case PULL_TOP:
                    Toast.makeText(getContext(), "已经是最新了", Toast.LENGTH_SHORT).show();
                    listView.refreshComplete();
                    break;
            }
        }
    };

    public IndexFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            channel = getArguments().getString("cato");
            // tmpl = getArguments().getInt("tmpl");
        }
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
        adapter.listView = listView;

        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pull = true;
                Map<String, Object> filters = new HashMap<>();
                filters.put("inc[>]", getActivity().getSharedPreferences("limit", Context.MODE_PRIVATE).getString("inc_max", ""));
                getArtPrama.filter = filters;
                post(getArtPrama, mIndexCallBack);
            }

            @Override
            public void onLoadMore() {
                push = true;
                Map<String, Object> filters = new HashMap<>();
                filters.put("inc[<]", getActivity().getSharedPreferences("limit", Context.MODE_PRIVATE).getString("inc_min", ""));
                getArtPrama.filter = filters;
                post(getArtPrama, mIndexCallBack);
            }
        });

        /*
        初始化请求参数
         */
        getArtPrama = new IndexRequestParams();
        getArtPrama.action = "get_articles";
        // getArtPrama.cato = 100000;
        getArtPrama.order = "inc DESC";
        getArtPrama.fields = new String[]{"inc", "star", "comt", "content", "good", "bad", "shar"};
        getArtPrama.rows = 20;
        getArtPrama.channel = channel;

        Map<String, Object> filters = new HashMap<>();

        filters.put("inc[>]", 0);
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

            try {
                String response_body = response.body().string();
                Log.e("######", response_body);
                JSONObject jsonObject = new JSONObject(response_body);
                if (jsonObject == null) {
                    //FIXME: 这个地方，如果出错了，那么就是服务器根本没有返回任何json数据
                    Log.e("######", response.body().string());

                    return;
                }

                System.out.println("----------response:" + jsonObject);
                int ret_status = jsonObject.getInt("status");
                if (ret_status == 100120) {
                    mHandler.sendEmptyMessage(PULL_TOP);
                }
                if (ret_status != 0) {
                    //FIXME: 这个地方，如果出错了，没有获得服务器的文章，那么就应该合适的告诉APP.不应该把错误蔓延下去。
                    Log.e("######", jsonObject.toString());

                    return;
                }

                //  JSONArray result = jsonObject.getJSONArray("result");
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                JSONArray result = jsonObject1.getJSONArray("data");

                //FIXME: 这个地方保存加载更新和加载更多的参数
                getActivity().getSharedPreferences("limit", Context.MODE_PRIVATE)
                        .edit()
                        .putString("inc_max", jsonObject1.getString("inc_max"))
                        .putString("inc_min", jsonObject1.getString("inc_min"))
                        .commit();
                //这个地方,我们把服务器回来的数据和result合并
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
     * 创建Fragment的实例
     *
     * @param chanel
     * @return
     */
    public static IndexFragment newInstance(String chanel) {
        IndexFragment fragment = new IndexFragment();
        Bundle args = new Bundle();
        args.putString("cato", chanel);
        fragment.setArguments(args);
        return fragment;
    }
}
