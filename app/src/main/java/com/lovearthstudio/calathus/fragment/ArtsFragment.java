package com.lovearthstudio.calathus.fragment;

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
import com.lovearthstudio.articles.core.MyCallBack;
import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.adapter.ArtsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_index)
public class ArtsFragment extends BaseFragment {

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

    private IndexCallBack mIndexCallBack;

    @ViewInject(R.id.list_view)
    private XRecyclerView xRecyclerView;

    private LinearLayoutManager linearLayoutManager;
    private ArtsAdapter adapter;

    /**
     * 整个页面的channel,rid.
     * */
    private String channel = "";
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
                    xRecyclerView.refreshComplete();
                    break;
                case PUSH:
                    adapter.notifyDataSetChanged();
                    xRecyclerView.loadMoreComplete();
                    break;
                case ARTSDK_ERROR:
                    Toast.makeText(getActivity(), "文章SDK错误", Toast.LENGTH_SHORT).show();
                    xRecyclerView.refreshComplete();
                    xRecyclerView.loadMoreComplete();

                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                    xRecyclerView.refreshComplete();
                    xRecyclerView.loadMoreComplete();
                    break;
                case SERVER_ERROR:
                    Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_SHORT).show();
                    xRecyclerView.refreshComplete();
                    xRecyclerView.loadMoreComplete();
                    break;
                case PULL_NOMORE:
                    Toast.makeText(getContext(), "已经是最新了", Toast.LENGTH_SHORT).show();
                    xRecyclerView.refreshComplete();
                    break;
                case PUSH_NOMORE:
                    Toast.makeText(getContext(), "已经是最旧了", Toast.LENGTH_SHORT).show();
                    xRecyclerView.loadMoreComplete();
                    break;
                case LOAD_NOMORE:
                    Toast.makeText(getContext(), "本地没有数据", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public ArtsFragment() {
        // Required empty public constructor
    }

    /**
     * 在viewpager里，左右移动一下频道页面，这个channel就更新一下，就调用一下onCreate事件
     *  在onCreate的时候注意，activity的参数有两种情况
     *  1：当初始化的时候，indexfragment只有channel这个参数，此时都出来的tid = 0
     *  2: 当进入详情界面的时候，indexfragemnt只有tid这个参数，此时估计channel都出来为""
     *  当第一次加载这个framgement的时候，会到数据库寻找最近的数据放置到jsonarray来
     * */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndexCallBack = new IndexCallBack();
        adapter = new ArtsAdapter(getActivity(), new JSONArray());
        if (getArguments() == null) {
            Log.e("ArtsFragment", "ArtsFragment given no arguments!");
            return;
        }
        channel = getArguments().getString("channel");
        rid = getArguments().getLong("rid");
        if (channel != null && !channel.equals("") && Constant.binder != null) {
            Log.i("Channel-" + channel, " try to Load articles with rid as " + rid);
            Constant.binder.getChannelArticles(channel, "load", 0/**tid*/, rid/**rid**/, mIndexCallBack);
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
        xRecyclerView.setLayoutManager(linearLayoutManager);
        xRecyclerView.setHasFixedSize(true);
        xRecyclerView.setAdapter(adapter);
        adapter.xRecyclerView = xRecyclerView;

        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pull = true;
                try {
                    if (Constant.binder != null){
                        Constant.binder.getChannelArticles(channel, "pull", adapter.maxTid,rid, mIndexCallBack);
                        Log.d("ArtsFragment","pull tid > "+adapter.maxTid);
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }

            @Override
            public void onLoadMore() {
                push = true;
                try {
                    if (Constant.binder != null){
                        Constant.binder.getChannelArticles(channel, "push",adapter.minTid,rid, mIndexCallBack);
                        Log.d("ArtsFragment","push tid < "+adapter.minTid);
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }
        });
    }

    class IndexCallBack implements MyCallBack {

        @Override
        public void onFailure(JSONObject failObject) {
            switch (failObject.optInt("status")) {
                case 1://网络错误
                    if (pull) {
                        pull = false;
                    }
                    if (push) {
                        push = false;
                    }
                    mHandler.sendEmptyMessage(NETWORK_ERROR);
                    break;
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
                long new_inc_min = response.optLong("inc_min");
                long new_inc_max = response.optLong("inc_max");
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
                    adapter.maxTid = new_inc_max;
                    Log.i("Channel-pull " + channel, "update tid [?,"+adapter.maxTid+"]");
                    mHandler.sendEmptyMessage(PULL);
                    Log.i("Channel-pull " + channel, "load article count " + articles.length() + " to " + adapter.jsonArray.length() + "articles with tid ["+adapter.minTid+","+adapter.maxTid+"]");
                } else if (push) {
                    if (articles == null || articles.length() == 0) {
                        mHandler.sendEmptyMessage(PUSH_NOMORE);
                        return;
                    }
                    for (int i = 0; i < articles.length(); i++) {
                        adapter.jsonArray.put(articles.get(i));
                    }
                    push = false;
                    adapter.minTid = new_inc_min;
                    Log.i("Channel-push " + channel, "update tid ["+adapter.minTid+",?]");
                    mHandler.sendEmptyMessage(PUSH);
                    Log.i("Channel-push " + channel, "load article count " + articles.length() + " to " + adapter.jsonArray.length() + "articles with tid ["+adapter.minTid+","+adapter.maxTid+"]");

                } else {
                    if (articles == null || articles.length() == 0) {
                        Log.i("Channel-load " + channel, "load article count " + articles.length());
                        mHandler.sendEmptyMessage(LOAD_NOMORE);
                        return;
                    }
                    for (int i = 0; i < articles.length(); i++) {
                        adapter.jsonArray.put(articles.get(i));
                    }

                    adapter.maxTid = new_inc_max;
                    adapter.minTid = new_inc_min;
                    Log.i("Channel-load " + channel, "update tid ["+adapter.minTid+","+adapter.maxTid+"]");
                    mHandler.sendEmptyMessage(UPDATE_DATA);
                    Log.i("Channel-load " + channel, "load article count " + articles.length() + " to " + adapter.jsonArray.length() + "articles with tid ["+adapter.minTid+","+adapter.maxTid+"]");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建一个ArtsFragment的实例
     * @param channel: 这个ArtsFragment对应的频道名字
     * @param rid : 这个artsFragment对应的root id数目
     * @return
     */
    public static ArtsFragment newInstance(String channel, long rid) {
        ArtsFragment fragment = new ArtsFragment();
        Bundle args = new Bundle();
        args.putString("channel", channel);
        args.putLong("rid", rid);
        fragment.setArguments(args);
        return fragment;
    }
}
