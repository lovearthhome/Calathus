package com.lovearthstudio.calathus.widget.cardview;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovearthstudio.calathus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文字的卡片布局
 * Created by zhaoliang on 16/5/12.
 */
public class TextsCard extends BaseCardView {

    private static final String TAG = "========" + TextsCard.class.getName();

    private TextView tv_texts_card;
    private LinearLayout ll_content;

    private Context context;

    public TextsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_texts, this, true);
    }

    @Override
    public void findView() {
        tv_texts_card = (TextView) findViewById(R.id.tv_texts_card);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
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

        tv_texts_card.setText(content_obj.optString("title"));

        JSONArray ja = content_obj.optJSONArray("texts");
        for (int i = 0; i < ja.length(); i++) {
            // TextView textView = new TextView(context);
            TextView textView = (TextView) View.inflate(context, R.layout.conver_tv_item, null);
//            textView.setText(Html.fromHtml(ja.get(i).toString().trim()));
            textView.setText(Html.fromHtml(ja.get(i).toString().replace("<p>", "").replace("</p>", "")));
            //textView.setBackgroundColor(0XFFFFFACD);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setPadding(44, 44, 44, 44);
            params.setMargins(20, 20, 20, 20);
            textView.setLayoutParams(params);
            ll_content.addView(textView);

        }
    }
}
