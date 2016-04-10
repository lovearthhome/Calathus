package com.zky.articleproj.adapter.holder.yimingyu;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

/**
 * Author：Mingyu Yi on 2016/4/10 15:10
 * Email：461072496@qq.com
 */
public class QuestionAnswerViewHolder extends BaseHolder{

    @ViewInject(R.id.question_answer_title)
    private TextView title;
    @ViewInject(R.id.question_html)
    private TextView question;
    @ViewInject(R.id.options_content)
    private LinearLayout options_content;
    @ViewInject(R.id.option_content)
    private LinearLayout option_content;
    @ViewInject(R.id.answer_html)
    private TextView answer;
    @ViewInject(R.id.why_html)
    private TextView why;

    public QuestionAnswerViewHolder(View itemView){
        super(itemView);
    }
    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        QuestionAnswerViewHolder holder = (QuestionAnswerViewHolder) baseHolder;
        JSONObject jsonObject=new JSONObject(jsonStr);
        holder.title.setText(jsonObject.getString("title"));

        String str =  jsonObject.getString("content");
        JSONObject jo=new JSONObject(str);
        holder.question.setText(Html.fromHtml(jo.getString("ask")));
        holder.answer.setText(Html.fromHtml(jo.getString("ans")));
        holder.why.setText(Html.fromHtml(jo.getString("why")));
        JSONArray ja=new JSONArray(jo.getString("options"));
        for (int i = 0; i <ja.length() ; i++) {
            JSONObject option=ja.getJSONObject(i);
            TextView name=new TextView(context);
            name.setText(option.getString("name"));
            holder.option_content.addView(name);
            String src=option.getString("src");
            if(!src.equals("")){
                ImageView img=new ImageView(context);
                Picasso.with(context).load(src).into(img);
                holder.option_content.addView(img);
            }
            holder.options_content.addView(option_content);
        }
    }

    @Override
    public int setLayoutFile() {
        return R.layout.question_anser_item;
    }
}
