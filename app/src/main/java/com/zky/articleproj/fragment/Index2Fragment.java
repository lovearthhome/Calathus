package com.zky.articleproj.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;
import com.zky.articleproj.R;
import com.zky.articleproj.base.BaseFragment;
import com.zky.articleproj.constant.Constant;
import com.zky.articleproj.domain.ArcState;
import com.zky.articleproj.domain.IndexRequestParams;
import com.zky.articleproj.domain.IndexResponse;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_index2)
public class Index2Fragment extends BaseFragment {

    private String[] imageurl = {"file:///android_asset/test/image1.jpg", "file:///android_asset/test/image2.jpg", "file:///android_asset/test/image3.jpg", "file:///android_asset/test/image4.jpg",
            "file:///android_asset/test/image5.jpg", "file:///android_asset/test/image6.jpg", "file:///android_asset/test/image7.jpg", "file:///android_asset/test/image8.jpg"};

    private static final int UPDATE_DATA = 1;
    private static final int PULL = 2;
    private static final int PUSH = 3;
    private static final int PARSE_DATA_ERROR = 4;

    private boolean pull;
    private boolean push;

    private OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private IndexRequestParams getArtPrama;

    @ViewInject(R.id.list_view)
    private XRecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private MyAdapter adapter;

    // 点赞+1动画
    private Animation animation;
    private ScaleAnimation scaleAnimation;
    private RotateAnimation rotateAnimation;
    private AnimationSet animationSet;

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


    public Index2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        动画
         */
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.applaud_animation);
        scaleAnimation = new ScaleAnimation(1, 2, 1, 2, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setDuration(1000);
        rotateAnimation = new RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotateAnimation.setDuration(1000);
        animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);

        /**
         * listview
         */
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setHasFixedSize(true);
        adapter = new MyAdapter(new ArrayList<IndexResponse.ResultBean>());
        listView.setAdapter(adapter);

        listView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pull = true;
                post(getArtPrama);
            }

            @Override
            public void onLoadMore() {
                push = true;
                post(getArtPrama);
            }
        });

        client = new OkHttpClient();

        /*
        初始化请求参数
         */
        getArtPrama = new IndexRequestParams();
        getArtPrama.dua_id = Constant.dua_id;
        getArtPrama.action = "get_articles";
        // getArtPrama.cato = 100000;
        getArtPrama.order = "create_time desc";
        getArtPrama.fields = new String[]{"inc", "brief", "star", "comt", "content", "title", "good", "bad", "shar"};
        getArtPrama.rows = 5;

        Map<String, Object> filters = new HashMap<>();
        //filters.put("media", 1);
        // filters.put("title", "");
        // filters.put("create_time[<]", 0);
        filters.put("cato", 100000);

        getArtPrama.filter = filters;

        post(getArtPrama);
    }

    /**
     * 请求数据
     */
    private void post(Object param) {
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
                // System.out.println("------------" + response.body().string());
                try {
                    IndexResponse indexResponse = com.alibaba.fastjson.JSON.parseObject(response.body().string(), IndexResponse.class);
                    System.out.println("-----------" + indexResponse.toString());
                    if (pull) {
                        adapter.dataEntities.addAll(0, indexResponse.getResult());
                        pull = false;
                        mHandler.sendEmptyMessage(PULL);
                    } else if (push) {
                        adapter.dataEntities.addAll(indexResponse.getResult());
                        push = false;
                        mHandler.sendEmptyMessage(PUSH);
                    }
                    adapter.dataEntities.addAll(indexResponse.getResult());
                    mHandler.sendEmptyMessage(UPDATE_DATA);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(PARSE_DATA_ERROR);
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        public List<IndexResponse.ResultBean> dataEntities;

        public MyAdapter(List<IndexResponse.ResultBean> dataEntities) {
            this.dataEntities = dataEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.index1_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            x.view().inject(vh, v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            IndexResponse.ResultBean item = dataEntities.get(position);
            //holder.tvTitle.setText(item.getTitle() == null ? "" : item.getTitle().toString());
            holder.item = item;
            if (position % 3 == 0) {
                holder.tvContent.setVisibility(View.GONE);
                holder.iv_gif.setVisibility(View.VISIBLE);

                Picasso.with(getActivity()).load(imageurl[position & 7]).into(holder.iv_gif);
            } else {
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.tvContent.setText(Html.fromHtml(item.getContent()));
                holder.iv_gif.setVisibility(View.GONE);
                // Ion.with(holder.iv_gif).load("http://p3.pstatp.com/large/16f000192821096479d");
            }
            holder.editer_name.setText(item.getEditor_name());
            String editor_avatar = item.getEditor_avatar();
            System.out.println("--------------icon:" + editor_avatar);
            Picasso.with(getActivity()).load(editor_avatar).into(holder.editer_icon);

            holder.tv_arrow.setText(item.getGood());
            holder.tv_narrow.setText(item.getBad());
            holder.tv_comment.setText(item.getComt());
            holder.tv_star.setText(item.getStar());
            holder.tv_share.setText(item.getShar());
        }

        @Override
        public int getItemCount() {
            return dataEntities == null ? 0 : dataEntities.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private IndexResponse.ResultBean item;
            private ArcState arcStateParam;
            private List<Map<String, Object>> events;
            private Map<String, Object> event;

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

            @ViewInject(R.id.editer_icon)
            private CircleImageView editer_icon;
            @ViewInject(R.id.editor_name)
            private TextView editer_name;

            @ViewInject(R.id.tv_arrow)
            private TextView tv_arrow;
            @ViewInject(R.id.tv_narrow)
            private TextView tv_narrow;
            @ViewInject(R.id.tv_comment)
            private TextView tv_comment;
            @ViewInject(R.id.tv_star)
            private TextView tv_star;
            @ViewInject(R.id.tv_share)
            private TextView tv_share;

            @Event({R.id.tv_arrow, R.id.tv_narrow, R.id.tv_comment, R.id.tv_star, R.id.tv_share})
            private void click(View view) {
                event.clear();
                events.clear();
                event.put("tid", item.getInc());
                event.put("param", 1);
                event.put("time", 1000);
                System.out.println(item.getInc());
                TextView textView = (TextView) view;
                textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) + 1));
                int id = view.getId();
                switch (id) {
                    case R.id.tv_arrow:
                        view.startAnimation(animationSet);
                        playAnimation(tv_arrow_animation);
                        event.put("act", "good");
                        break;
                    case R.id.tv_narrow:
                        view.startAnimation(animationSet);
                        playAnimation(tv_narrow_animation);
                        event.put("act", "bad");
                        break;
                    case R.id.tv_comment:
                        event.put("act", "comt");
                        break;
                    case R.id.tv_star:
                        view.startAnimation(animationSet);
                        playAnimation(tv_star_animation);
                        event.put("act", "star");
                        break;
                    case R.id.tv_share:
                        event.put("act", "shar");
                        break;
                }
                events.add(event);
                arcStateParam.events = events;
                postArtState(arcStateParam);
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

                arcStateParam = new ArcState();
                arcStateParam.dua_id = Constant.dua_id;
                arcStateParam.action = "log_articles";
                event = new HashMap<>();
                events = new ArrayList<>();
            }
        }

    }

    /**
     * 请求数据
     */
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
    }
}
