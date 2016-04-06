package com.zky.articleproj.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.yimingyu.android.lib.duasdk.Dua;
import com.zky.articleproj.R;
import com.zky.articleproj.base.BaseActivity;
import com.zky.articleproj.constant.Constant;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.other_toolbar)
    private Toolbar toolbar;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    Dua dua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dua = new Dua(this);

        client = new OkHttpClient();

        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mEmailView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    mEmailView.setError("请输入电话号码");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setText("请输入密码");
                    return;
                }
//                dua.login();
            }
        });
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

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                System.out.println("------------" + response.body().string());
            }
        });
    }
}

