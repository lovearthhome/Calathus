package com.zky.articleproj.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zky.articleproj.R;
import com.zky.articleproj.base.BaseFragment;
import com.zky.articleproj.constant.Constant;
import com.zky.articleproj.domain.IndexRequestParams;
import com.zky.articleproj.domain.IndexResponse;
import com.zky.articleproj.widget.SuperSwipeRefreshLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_index)
public class IndexFragment extends BaseFragment {

    private static final String TAG = IndexFragment.class.getName();

    private static final int UPDATE_DATA = 1;
    private static final int PULL = 2;
    private static final int PUSH = 3;
    private OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private IndexRequestParams param;

    @ViewInject(R.id.list_view)
    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private MyAdapter adapter;

    @ViewInject(R.id.swipe_refresh)
    private SuperSwipeRefreshLayout swipeRefreshLayout;

    private boolean pull;
    private boolean push;

    // Header View
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    // Footer View
    private ProgressBar footerProgressBar;
    private TextView footerTextView;
    private ImageView footerImageView;

    // 点赞+1动画
    private Animation animation;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DATA:
                    adapter.notifyDataSetChanged();
                    break;
                case PULL:
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    break;
                case PUSH:
                    footerImageView.setVisibility(View.VISIBLE);
                    footerProgressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setLoadMore(false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.applaud_animation);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setHasFixedSize(true);
        //   adapter = new MyAdapter(new ArrayList<IndexResponse.DataEntity>());
        listView.setAdapter(adapter);

        client = new OkHttpClient();


        /*
        初始化请求参数
         */
        param = new IndexRequestParams();
        param.dua_id = 41;
        param.action = "get_articles";
        param.order = "create_time desc";
        //param.fields = "[\"brief\",\"stars\",\"comts\"]";
        param.rows = 20;

        Map<String, Object> filters = new HashMap<>();
        filters.put("time", System.currentTimeMillis());
        filters.put("media", 8);
        filters.put("title", "");
        filters.put("\"create_time[<]\"", 0);

        post();

        // init SuperSwipeRefreshLayout
        swipeRefreshLayout.setHeaderViewBackgroundColor(0xff888888);
        swipeRefreshLayout.setHeaderView(createHeaderView());// add headerView
        swipeRefreshLayout.setFooterView(createFooterView());
        swipeRefreshLayout.setTargetScrollWithLayout(true);
        swipeRefreshLayout
                .setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {

                    @Override
                    public void onRefresh() {
                        textView.setText("正在刷新");
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);

                        pull = true;
                        post();
                    }

                    @Override
                    public void onPullDistance(int distance) {
                        // pull distance
                    }

                    @Override
                    public void onPullEnable(boolean enable) {
                        textView.setText(enable ? "松开刷新" : "下拉刷新");
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setRotation(enable ? 180 : 0);
                    }
                });

        swipeRefreshLayout
                .setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {

                    @Override
                    public void onLoadMore() {
                        footerTextView.setText("正在加载...");
                        footerImageView.setVisibility(View.GONE);
                        footerProgressBar.setVisibility(View.VISIBLE);

                        push = true;
                        post();
                    }

                    @Override
                    public void onPushEnable(boolean enable) {
                        footerTextView.setText(enable ? "松开加载" : "上拉加载");
                        footerImageView.setVisibility(View.VISIBLE);
                        footerImageView.setRotation(enable ? 0 : 180);
                    }

                    @Override
                    public void onPushDistance(int distance) {
                        // TODO Auto-generated method stub

                    }

                });
    }

    /**
     * 请求数据
     */
    private void post() {
        String requestParams = com.alibaba.fastjson.JSON.toJSONString(param);
        System.out.println("---------" + requestParams);
        Log.i(TAG, requestParams);
        RequestBody body = RequestBody.create(JSON, requestParams);
        Request request = new Request.Builder()
                .url(Constant.baseUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                IndexResponse indexResponse = com.alibaba.fastjson.JSON.parseObject(response.body().string(), IndexResponse.class);
//                if (pull) {
//
//                    adapter.dataEntities.addAll(0, indexResponse.getData());
//                    pull = false;
//                    mHandler.sendEmptyMessage(PULL);
//                } else if (push) {
//                    adapter.dataEntities.addAll(indexResponse.getData());
//                    push = false;
//                    mHandler.sendEmptyMessage(PUSH);
//                }
//                adapter.dataEntities.addAll(indexResponse.getData());
//                mHandler.sendEmptyMessage(UPDATE_DATA);
            }


        });
    }

    private View createFooterView() {
        View footerView = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_footer, null);
        footerProgressBar = (ProgressBar) footerView
                .findViewById(R.id.footer_pb_view);
        footerImageView = (ImageView) footerView
                .findViewById(R.id.footer_image_view);
        footerTextView = (TextView) footerView
                .findViewById(R.id.footer_text_view);
        footerProgressBar.setVisibility(View.GONE);
        footerImageView.setVisibility(View.VISIBLE);
        footerImageView.setImageResource(R.drawable.down_arrow);
        footerTextView.setText("上拉加载更多...");
        return footerView;
    }

    private View createHeaderView() {
        View headerView = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_head, null);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        textView = (TextView) headerView.findViewById(R.id.text_view);
        textView.setText("下拉刷新");
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.down_arrow);
        progressBar.setVisibility(View.GONE);
        return headerView;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

//        public List<IndexResponse.DataEntity> dataEntities;
//
//        public MyAdapter(List<IndexResponse.DataEntity> dataEntities) {
//            this.dataEntities = dataEntities;
//        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.index1_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            x.view().inject(vh, v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
//            IndexResponse.DataEntity item = dataEntities.get(position);
//            holder.tvTitle.setText(item.getTitle());
//            if (position % 3 == 0) {
//                holder.tvContent.setVisibility(View.GONE);
//                holder.iv_gif.setVisibility(View.VISIBLE);
//
//            } else {
//                holder.tvContent.setVisibility(View.VISIBLE);
//                holder.tvContent.setText(item.getContent());
//                holder.iv_gif.setVisibility(View.GONE);
//                // Ion.with(holder.iv_gif).load("http://p3.pstatp.com/large/16f000192821096479d");
//            }
        }

        @Override
        public int getItemCount() {
            //return dataEntities.size();
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @ViewInject(R.id.tv_index1_title)
            private TextView tvTitle;
            @ViewInject(R.id.tv_index1_content)
            private TextView tvContent;

            @ViewInject(R.id.tv_arrow_animation)
            private TextView tv_arrow_animation;
            @ViewInject(R.id.tv_narrow_animation)
            private TextView tv_narrow_animation;
            @ViewInject(R.id.tv_star_animation)
            private TextView tv_star_animation;

            @ViewInject(R.id.iv_gif)
            private ImageView iv_gif;

            @Event({R.id.tv_arrow, R.id.tv_narrow, R.id.tv_comment, R.id.tv_star, R.id.tv_share})
            private void click(View view) {
                switch (view.getId()) {
                    case R.id.tv_arrow:
                        playAnimation(tv_arrow_animation);
                        break;
                    case R.id.tv_narrow:
                        playAnimation(tv_narrow_animation);
                        break;
                    case R.id.tv_comment:

                        break;
                    case R.id.tv_star:
                        playAnimation(tv_star_animation);
                        break;
                    case R.id.tv_share:

                        break;
                }
            }

            private void playAnimation(final View view) {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animation);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        view.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
            }

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }

    }
}