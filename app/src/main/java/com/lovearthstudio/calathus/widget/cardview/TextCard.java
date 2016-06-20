package com.lovearthstudio.calathus.widget.cardview;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lovearthstudio.calathus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文字的卡片布局
 * Created by zhaoliang on 16/5/12.
 */
public class TextCard extends BaseCardView {

    private static final String TAG = "========" + TextCard.class.getName();

    private TextView tv_text_title;
    private TextView tv_text_content;

    public TextCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_text, this, true);
    }

    @Override
    public void findView() {
        tv_text_title = (TextView) findViewById(R.id.tv_text_title);
        tv_text_content = (TextView) findViewById(R.id.tv_text_content);
    }

    @Override
    public void setOnClickListener() {

    }

    @Override
    public void parseData(String jsonStr) throws JSONException {

        Log.i(TAG, jsonStr);

        JSONObject jsonObject = new JSONObject(jsonStr);
        String content_str = jsonObject.getString("content");
        JSONObject content_obj = new JSONObject(content_str);

        String title = content_obj.optString("title");

        if ("null".equals(title) || TextUtils.isEmpty(title)) {
            tv_text_title.setVisibility(View.GONE);
        } else {
            tv_text_title.setText(title);
        }
        JSONArray ja = content_obj.optJSONArray("texts");
        String text = ja.get(0).toString();
        tv_text_content.setText(Html.fromHtml(text));
    }
}
