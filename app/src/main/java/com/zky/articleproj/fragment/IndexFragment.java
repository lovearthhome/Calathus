package com.zky.articleproj.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lovearthstudio.articles.constant.Constant;
import com.lovearthstudio.articles.net.IndexRequestParams;
import com.lovearthstudio.articles.net.MyCallBack;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.adapter.IndexListAdapter;
import com.zky.articleproj.base.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_index)
public class IndexFragment extends BaseFragment {

    private static final int UPDATE_DATA = 1;
    private static final int PULL = 2;
    private static final int PUSH = 3;
    private static final int PARSE_DATA_ERROR = 4;
    private static final int PULL_NOMORE = 5;
    private static final int PUSH_NOMORE = 6;
    private static final int LOAD_NOMORE = 7;


    private boolean pull;
    private boolean push;

    private IndexRequestParams getArtPrama;
    private IndexCallBack mIndexCallBack;

    @ViewInject(R.id.list_view)
    private XRecyclerView listView;

    private LinearLayoutManager linearLayoutManager;
    private IndexListAdapter adapter;

    private String channel = "";

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
                case PULL_NOMORE:
                    Toast.makeText(getContext(), "已经是最新了", Toast.LENGTH_SHORT).show();
                    listView.refreshComplete();
                    break;
                case PUSH_NOMORE:
                    Toast.makeText(getContext(), "已经是最旧了", Toast.LENGTH_SHORT).show();
                    listView.loadMoreComplete();
                    break;
                case LOAD_NOMORE:
                    Toast.makeText(getContext(), "本地没有数据", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public IndexFragment() {
        // Required empty public constructor
    }

    //在viewpager里，左右移动一下频道页面，这个channel就更新一下，就调用一下onCreate事件
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置频道
        if (getArguments() != null) {
            channel = getArguments().getString("channel");
            // tmpl = getArguments().getInt("tmpl");
        }

        Log.i("Fragment", "onCreate      " + channel);
        //getActivity().bindService(new Intent(getActivity(), ArticleService.class), new RomoteServiceConnection(), Context.BIND_AUTO_CREATE);
        mIndexCallBack = new IndexCallBack();
        adapter = new IndexListAdapter(getActivity(), new JSONArray());
        //当第一次加载这个framgement的时候，会到数据库寻找最近的数据放置到jsonarray来
        Log.i("Channel-"+channel, " try to Load article of " + channel);
        if (Constant.binder != null) {
            Log.i("Channel-"+channel, "do  to Load article of " + channel);
            Constant.binder.getData(channel, "load", 0, mIndexCallBack);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Channel", "onCreateView      " + channel);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Channel", "onDestroy           " + channel);
    }

    @Override
    public void onDestroyView() {
        Log.i("Channel", "onDestroyView      " + channel);
        super.onDestroyView();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("Channel", "onViewCreate     " + channel);
        /**
         * listview
         */
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setHasFixedSize(true);
        listView.setAdapter(adapter);
        adapter.listView = listView;

        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pull = true;
                try {
                    long tidref = 0;
                    int artCount = adapter.jsonArray.length();
                    if (artCount != 0) {
                        Log.i("request", "jsonarray has articles:" + artCount);
                        JSONObject jsonObject = new JSONObject(adapter.jsonArray.get(0).toString());
                        tidref = jsonObject.getInt("inc");
                    }
                    if(Constant.binder != null)
                    Constant.binder.getData(channel, "pull", tidref, mIndexCallBack);
                } catch (JSONException e) {
                    Log.e("jsonError", e.toString());
                }
            }

            @Override
            public void onLoadMore() {
                push = true;
                try {
                    int maxi = adapter.jsonArray.length() - 1;
                    if (maxi < 0) maxi = 0;
                    JSONObject jsonObject = new JSONObject(adapter.jsonArray.get(maxi).toString());
                    long tid = jsonObject.getInt("inc");
                    if(Constant.binder != null)
                    Constant.binder.getData(channel, "push", tid, mIndexCallBack);
                } catch (JSONException e) {
                    Log.e("jsonError", e.toString());
                }
            }
        });
    }

    class IndexCallBack implements MyCallBack {

        @Override
        public void onFailure(String reason) {
            mHandler.sendEmptyMessage(PARSE_DATA_ERROR);
        }

        @Override
        public void onResponse(JSONArray articles) {
            try {
                //这个地方,我们把服务器回来的数据和result合并
                if (pull) {
                    if (articles == null || articles.length() == 0) {
                        mHandler.sendEmptyMessage(PULL_NOMORE);
                        return;
                    }
                    for (int i = 0; i < adapter.jsonArray.length(); i++) {
                        articles.put(adapter.jsonArray.get(i));
                    }
                    pull = false;
                    adapter.jsonArray = articles;
                    mHandler.sendEmptyMessage(PULL);
                    Log.i("Channel-push "+channel, "load article count "+articles.length()+" to "+adapter.jsonArray.length() + "articles");
                } else if (push) {
                    if (articles == null || articles.length() == 0) {
                        mHandler.sendEmptyMessage(PUSH_NOMORE);
                        return;
                    }
                    for (int i = 0; i < articles.length(); i++) {
                        adapter.jsonArray.put(articles.get(i));
                    }
                    push = false;
                    mHandler.sendEmptyMessage(PUSH);

                    Log.i("Channel-push "+channel, "load article count "+articles.length()+" to "+adapter.jsonArray.length() + "articles");
                    //System.out.println("----------:push");
                } else {

                    if (articles == null || articles.length() == 0) {
                        Log.i("Channel-load "+channel, "load article count "+articles.length());
                        mHandler.sendEmptyMessage(LOAD_NOMORE);
                        return;
                    }
                    for (int i = 0; i < articles.length(); i++) {
                        adapter.jsonArray.put(articles.get(i));
                    }
                    mHandler.sendEmptyMessage(UPDATE_DATA);
                    Log.i("Channel-load "+channel, "load article count "+articles.length()+" to "+adapter.jsonArray.length() + "articles");
                    //System.out.println("-----------:load");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建Fragment的实例
     *
     * @param channel
     * @return
     */
    public static IndexFragment newInstance(String channel) {
        IndexFragment fragment = new IndexFragment();
        Bundle args = new Bundle();
        args.putString("channel", channel);
        fragment.setArguments(args);
        return fragment;
    }

//    class RomoteServiceConnection implements ServiceConnection {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Constant.binder = (ArticleService.ArticleBinder) service;
//            Constant.binder.getData(channel, "load", 0, mIndexCallBack);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    }
}
