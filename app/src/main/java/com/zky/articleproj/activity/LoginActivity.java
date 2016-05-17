package com.zky.articleproj.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.DuaCallback;
import com.lovearthstudio.duasdk.util.LogUtil;
import com.zky.articleproj.R;
import com.zky.articleproj.base.BaseActivity;
import com.zky.articleproj.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                phone="14789568702";
                password="111111";
                if (TextUtils.isEmpty(phone)) {
                    mEmailView.setError("请输入电话号码");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setText("请输入密码");
                    return;
                }
                Dua.getInstance().login("+86-" + phone, password, "reviewer", new DuaCallback() {
                    @Override
                    public void onSuccess(String s) {
                        try {
                            JSONObject jo=new JSONObject(s);
                            Constant.rules=new Gson().fromJson(jo.getString("rules"),new TypeToken<ArrayList<String>>() {}.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }

                    @Override
                    public void onError(String s) {
                        LogUtil.e(s);
                    }
                });
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

//    /**
//     * 请求数据
//     */
//    private void postArtState(Object param) {
//        String requestParams = com.alibaba.fastjson.JSON.toJSONString(param);
//        //System.out.println("---------" + requestParams);
//        RequestBody body = RequestBody.create(JSON, requestParams);
//        Request request = new Request.Builder()
//                .url(Constant.baseUrl)
//                .post(body)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, Response response) throws IOException {
//                //System.out.println("------------" + response.body().string());
//            }
//        });
//    }
}

