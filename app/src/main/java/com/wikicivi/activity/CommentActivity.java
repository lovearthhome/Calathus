package com.wikicivi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chaowen.commentlibrary.CommentView;
import com.lovearthstudio.articles.net.MyCallBack;
import com.wikicivi.R;
import com.wikicivi.fragment.IndexFragment;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_common)
public class CommentActivity extends BaseActivity {

    private long tid = 0;
    private String channel;


    @ViewInject(R.id.other_toolbar)
    private Toolbar toolbar;

    @ViewInject(R.id.compose)
    private CommentView compose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compose.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(String text) {
                System.out.println("评论内容:" + text);
            }

            @Override
            public void onSendVoice(String file, int length) {

            }

            @Override
            public void onSendImageClicked(View v) {

            }

            @Override
            public void onSendLocationClicked(View v) {

            }
        });

        Intent intent = getIntent();
        channel = intent.getStringExtra("channel");
        tid = intent.getLongExtra("tid", 0);

        toolbar.setTitle(getResources().getString(R.string.settings_comment));
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.root_view, IndexFragment.newInstance(channel, tid)).commit();
    }

    /*@Event({R.id.btn_comment})
    private void click(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                System.out.println("-----------------");
                if (Constant.binder != null)
                {
                    if(!Dua.getInstance().getCurrentDuaUser().logon){
                        startActivity(new Intent(CommentActivity.this, DuaActivityLogin.class));
                    }else{
                        //from,tid,cato, media,flag , tmpl,content, myCallBack
                        Constant.binder.addArticle(tid,"Comment","Text",101,601,"{\"title\":\"\",\"brief\":\"\",\"texts\":[\"。 哈哈哈，评论第一行<br>　评论第二行：<br>　　“评论里引用别人的话”\"],\"files\":[]}", new addArticleCallBack());
                    }
                }

                break;
        }
    }*/
    class addArticleCallBack implements MyCallBack {

        @Override
        public void onFailure(JSONObject reason) {
            System.out.println("发送评论失败" + reason.toString());
        }

        @Override
        public void onResponse(JSONObject result) {
            System.out.println("发送评论成功" + result.toString());
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
