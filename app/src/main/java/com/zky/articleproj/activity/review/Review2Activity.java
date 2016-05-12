package com.zky.articleproj.activity.review;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lovearthstudio.articles.net.IndexRequestParams;
import com.zky.articleproj.R;
import com.zky.articleproj.activity.review.item.ImageFragment;
import com.zky.articleproj.activity.review.item.MusicFragment;
import com.zky.articleproj.activity.review.item.TextFragment;
import com.zky.articleproj.activity.review.item.VideoFragment;
import com.zky.articleproj.base.BaseActivity;
import com.zky.articleproj.net.NetCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

@ContentView(R.layout.activity_review2)
public class Review2Activity extends BaseActivity {

    @ViewInject(R.id.other_toolbar)
    private Toolbar toolbar;

    @ViewInject(R.id.review_fl_content)
    private FrameLayout review_fl_content;

    @ViewInject(R.id.card_left_btn)
    private Button card_left_btn;

    @ViewInject(R.id.card_right_btn)
    private Button card_right_btn;

    private IndexRequestParams getArtParams;
    private long artId;
    private int tmpl;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = (String) msg.obj;
            switch (msg.what) {
                case 101:
                    getSupportFragmentManager().beginTransaction().replace(R.id.review_fl_content, TextFragment.newInstance(data)).commit();
                    break;
                case 201:
                    getSupportFragmentManager().beginTransaction().replace(R.id.review_fl_content, ImageFragment.newInstance(data)).commit();
                    break;
                case 301:
                    getSupportFragmentManager().beginTransaction().replace(R.id.review_fl_content, MusicFragment.newInstance(data)).commit();
                    break;
                case 401:
                    getSupportFragmentManager().beginTransaction().replace(R.id.review_fl_content, VideoFragment.newInstance(data)).commit();
                    break;
                default:
                    Toast.makeText(Review2Activity.this, "不可预料的模板: " + tmpl, Toast.LENGTH_SHORT).show();
            }
        }
    };

    NetCallBack reviewCallBack = new NetCallBack() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                JSONObject jsonResult = new JSONObject(jsonResponse.optString("result"));
                JSONArray data = jsonResult.optJSONArray("data");
                String strData0 = data.get(0).toString();
                JSONObject jsodata0 = new JSONObject(strData0);

                System.out.println("==========稿子数据" + jsonResult);

                tmpl = jsodata0.getInt("tmpl");
                artId = jsodata0.getLong("inc");

                Message obtain = Message.obtain();
                obtain.what = tmpl;
                obtain.obj = strData0;
                mHandler.sendMessage(obtain);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Event({R.id.card_left_btn, R.id.card_right_btn})
    private void click(View view) {
        switch (view.getId()) {
            // FIXME:审稿不通过, 通知服务器不通过的稿子的id, 并请求新的审稿
            case R.id.card_left_btn:
                post(getArtParams, reviewCallBack);
                break;

            // FIXME:审稿通过, 通知服务器通过的稿子的id, 并请求新的审稿
            case R.id.card_right_btn:
                post(getArtParams, reviewCallBack);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setTitle(getResources().getString(R.string.settings_review_title));
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         /*
        初始化请求参数
         */
        getArtParams = new IndexRequestParams();
        getArtParams.action = "get_reviewArtile";
        getArtParams.order = "inc DESC";
        getArtParams.fields = new String[]{"inc", "star", "comt", "content", "good", "bad", "shar"};
        getArtParams.rows = 1;
        getArtParams.channel = "All";
        getArtParams.filter = new HashMap<>();
        getArtParams.filter.put("inc[>]", 0);

        post(getArtParams, reviewCallBack);
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
}
