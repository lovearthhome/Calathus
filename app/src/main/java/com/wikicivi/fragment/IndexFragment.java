package com.wikicivi.fragment;


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
import com.lovearthstudio.articles.net.GetArtParams;
import com.lovearthstudio.articles.net.MyCallBack;
import com.wikicivi.R;
import com.wikicivi.adapter.adapter.IndexListAdapter;

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
    private static final int ARTSDK_ERROR = 4;
    private static final int NETWORK_ERROR = 5;
    private static final int SERVER_ERROR = 6;

    private static final int PULL_NOMORE = 8;
    private static final int PUSH_NOMORE = 9;
    private static final int LOAD_NOMORE = 10;


    private boolean pull;
    private boolean push;

    private GetArtParams getArtPrama;
    private IndexCallBack mIndexCallBack;

    @ViewInject(R.id.list_view)
    private XRecyclerView listView;

    private LinearLayoutManager linearLayoutManager;
    private IndexListAdapter adapter;

    /**
     * 整个页面的channel，tid,rid.
     * */
    private String channel = "";
    private long tid = 0;
    private long rid = 0;

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
                case ARTSDK_ERROR:
                    Toast.makeText(getActivity(), "文章SDK错误", Toast.LENGTH_SHORT).show();
                    listView.refreshComplete();
                    listView.loadMoreComplete();

                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                    listView.refreshComplete();
                    listView.loadMoreComplete();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                    listView.refreshComplete();
                    listView.loadMoreComplete();
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
    /**
     *  在onCreate的时候注意，activity的参数有两种情况
     *  1：当初始化的时候，indexfragment只有channel这个参数，此时都出来的tid = 0
     *  2: 当进入详情界面的时候，indexfragemnt只有tid这个参数，此时估计channel都出来为""
     *
     * */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndexCallBack = new IndexCallBack();
        adapter = new IndexListAdapter(getActivity(), new JSONArray());

        // 设置频道
        if (getArguments() != null) {
            channel = getArguments().getString("channel");
            rid = getArguments().getLong("rid");
            if(channel != null && !channel.equals("") )
            {
                //当第一次加载这个framgement的时候，会到数据库寻找最近的数据放置到jsonarray来
                Log.i("Channel-" + channel, " try to Load article of " + channel+" with rid as "+rid);
                if (Constant.binder != null) {
                    Log.i("Channel-" + channel, "do  to Load article of " + channel);
                    Constant.binder.getChannelArticles(channel, "load", 0/**tid*/,rid/**rid**/, mIndexCallBack);
                }
            }
        }else{
            Log.e("Channel", "indexfragment没有参数");
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
                    if (artCount > 0) {
                        Log.i("request", "jsonarray has articles:" + artCount);
                        JSONObject jsonObject = new JSONObject(adapter.jsonArray.get(0).toString());
                        tidref = jsonObject.getInt("inc");
                        /**如果本页面是个评论页面，那么tidref是第1项*/
                        if("Comment".equals(channel) && artCount > 1)
                        {
                            JSONObject jsonObject1 = new JSONObject(adapter.jsonArray.get(1).toString());
                            tidref =  jsonObject1.getInt("inc");
                        }
                    }
                    if (Constant.binder != null)
                        Constant.binder.getChannelArticles(channel, "pull", tidref,rid, mIndexCallBack);
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
                    if (Constant.binder != null)
                        Constant.binder.getChannelArticles(channel, "push", tid,rid, mIndexCallBack);
                } catch (JSONException e) {
                    Log.e("jsonError", e.toString());
                }
            }
        });
    }

    class IndexCallBack implements MyCallBack {

        @Override
        public void onFailure(JSONObject failObject) {
            switch (failObject.optInt("status")) {
                case 1://网络错误
                case 6://ARTSDK内部例外
                    if (pull) {
                        pull = false;
                    }
                    if (push) {
                        push = false;
                    }
                    mHandler.sendEmptyMessage(ARTSDK_ERROR);
                    break;
                case 2://ArticleSDKn内部不可识别的错误
                    if (pull) {
                        pull = false;
                    }
                    if (push) {
                        push = false;
                    }
                    mHandler.sendEmptyMessage(ARTSDK_ERROR);
                    break;
                case 3://ArticleSDK数据解析错误,这个错误需要调试才能知道是客户端的错还是服务器端的错
                    if (pull) {
                        pull = false;
                    }
                    if (push) {
                        push = false;
                    }
                    mHandler.sendEmptyMessage(ARTSDK_ERROR);
                    break;
                case 4://ArticleSDK数据解析错误,这个错误需要调试才能知道是客户端的错还是服务器端的错
                    if (pull) {
                        pull = false;
                    }
                    if (push) {
                        push = false;
                    }
                    mHandler.sendEmptyMessage(SERVER_ERROR);
                    break;
                case 5://ArticleSDK数据解析错误,这个错误需要调试才能知道是客户端的错还是服务器端的错
                    if (pull) {
                        mHandler.sendEmptyMessage(PULL_NOMORE);
                    }
                    if (push) {
                        mHandler.sendEmptyMessage(PUSH_NOMORE);
                    }
                    break;
                default:
                    mHandler.sendEmptyMessage(ARTSDK_ERROR);
                    break;
            }

        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONArray articles = response.optJSONArray("data");
                //这个地方,我们把服务器回来的数据和result合并
                if (pull) {
                    if (articles == null || articles.length() == 0) {
                        mHandler.sendEmptyMessage(PULL_NOMORE);
                        return;
                    }
                    JSONArray sumArticles = new JSONArray();
                    if("Comment".equals(channel) && articles.length() > 0)
                    {
                        if(adapter.jsonArray.length() > 0)
                        {
                            sumArticles.put( adapter.jsonArray.get(0));
                        }

                        if(articles.length() > 0)
                        {
                            for (int i = 0; i < articles.length(); i++) {
                                sumArticles.put(articles.get(i));
                            }
                        }
                        if(adapter.jsonArray.length() > 1)
                        {
                            for (int i = 1; i < adapter.jsonArray.length(); i++) {
                                sumArticles.put(adapter.jsonArray.get(i));
                            }
                        }
                    }else{
                        sumArticles = articles;
                        if(adapter.jsonArray.length() > 1)
                        {
                            for (int i = 1; i < adapter.jsonArray.length(); i++) {
                                sumArticles.put(adapter.jsonArray.get(i));
                            }
                        }
                    }



                    pull = false;
                    adapter.jsonArray = sumArticles;
                    mHandler.sendEmptyMessage(PULL);
                    Log.i("Channel-push " + channel, "load article count " + articles.length() + " to " + adapter.jsonArray.length() + "articles");
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
                    Log.i("Channel-push " + channel, "load article count " + articles.length() + " to " + adapter.jsonArray.length() + "articles");
                    //System.out.println("----------:push");
                } else {
                    if (articles == null || articles.length() == 0) {
                        Log.i("Channel-load " + channel, "load article count " + articles.length());
                        mHandler.sendEmptyMessage(LOAD_NOMORE);
                        return;
                    }
                    for (int i = 0; i < articles.length(); i++) {
                        adapter.jsonArray.put(articles.get(i));
                    }
                    mHandler.sendEmptyMessage(UPDATE_DATA);
                    Log.i("Channel-load " + channel, "load article count " + articles.length() + " to " + adapter.jsonArray.length() + "articles");
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
    public static IndexFragment newInstance(String channel,long rid) {
        IndexFragment fragment = new IndexFragment();
        Bundle args = new Bundle();
        args.putString("channel", channel);
        args.putLong("rid", rid);
        fragment.setArguments(args);
        return fragment;
    }



}
