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

import com.lovearthstudio.articles.constant.Constant;
import com.lovearthstudio.articles.net.GetArtParams;
import com.lovearthstudio.articles.net.MyCallBack;
import com.lovearthstudio.duasdk.util.JsonUtil;
import com.lovearthstudio.duasdk.util.LogUtil;
import com.zky.articleproj.R;
import com.zky.articleproj.activity.review.item.ImageFragment;
import com.zky.articleproj.activity.review.item.MusicFragment;
import com.zky.articleproj.activity.review.item.TextFragment;
import com.zky.articleproj.activity.review.item.VideoFragment;
import com.zky.articleproj.base.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

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

    @ViewInject(R.id.card_home_button)
    private Button card_home_button;

    private GetArtParams getArtParams;
    private long artId;
    private int tmpl;
    private getReviewArticleCallBack getReviewArticleCB;
    private setReviewArticleCallBack setReviewArticleCB;

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
                case 0:
                    Toast.makeText(Review2Activity.this, data, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(Review2Activity.this, "不可预料的模板: " + tmpl, Toast.LENGTH_SHORT).show();
            }
        }
    };


    class getReviewArticleCallBack implements MyCallBack {

        @Override
        public void onFailure(JSONObject reason) {

        }

        @Override
        public void onResponse(JSONObject result) {
            try {
                JSONArray articles = result.getJSONArray("data");
                if(articles.length() == 0)
                {
                    Message obtain = Message.obtain();
                    obtain.what = 0;
                    obtain.obj = "没有文章了！";
                    mHandler.sendMessage(obtain);
                    return;
                }else{
                    String artStr = articles.get(0).toString();
                    JSONObject artJson= JsonUtil.toJsonObject(artStr);
                    JSONObject contentJson = artJson.getJSONObject("content");
                    tmpl  = artJson.optInt("tmpl");
                    artId = artJson.optLong("inc");
                    if(artJson != null){
                        LogUtil.e(artJson.toString());
                        String url=artJson
                                .getJSONObject("content")
                                .getJSONArray("files")
                                .getJSONObject(0)
                                .getJSONArray("farray")
                                .getJSONObject(0)
                                .getString("src");
                        LogUtil.e(url);
                    }

                    Message obtain = Message.obtain();
                    obtain.what = tmpl;
                    obtain.obj = artStr;
                    mHandler.sendMessage(obtain);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class setReviewArticleCallBack implements MyCallBack {

        @Override
        public void onFailure(JSONObject reason) {

        }

        @Override
        public void onResponse(JSONObject result) {
            try {
                //FIXME: do nothing
                int good = result.getInt("good");
                int bad = result.getInt("bad");
                if(result !=null)
                {
                    Message obtain = Message.obtain();
                    obtain.what = 0;
                    obtain.obj = "good:bad = "+good+"/"+bad;
                    mHandler.sendMessage(obtain);
                }
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Event({R.id.card_left_btn, R.id.card_right_btn,R.id.card_home_button})
    private void click(View view) {
        switch (view.getId()) {
            // FIXME:审稿不通过, 通知服务器不通过的稿子的id, 并请求新的审稿
            case R.id.card_left_btn:
                //当第一次加载这个Activity的时候，会加载一个审阅类文章
                if (Constant.binder != null) {
                    Constant.binder.setReviewArticle(artId,1/*通过*/, setReviewArticleCB);
                    Constant.binder.getReviewArticle(getReviewArticleCB);
                }
                break;

            // FIXME:审稿通过, 通知服务器通过的稿子的id, 并请求新的审稿
            case R.id.card_right_btn:
                if (Constant.binder != null) {
                    Constant.binder.setReviewArticle(artId,0/*通过*/, setReviewArticleCB);
                    Constant.binder.getReviewArticle(getReviewArticleCB);
                }
                break;

            case R.id.card_home_button:
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

        getReviewArticleCB = new getReviewArticleCallBack();
        setReviewArticleCB = new setReviewArticleCallBack();




        //当第一次加载这个Activity的时候，会加载一个审阅类文章
        if (Constant.binder != null) {
            Constant.binder.getReviewArticle(getReviewArticleCB);
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
}
