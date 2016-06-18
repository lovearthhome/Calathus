package com.wikicivi.holder;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.wikicivi.R;
import com.wikicivi.widget.ScratchCardView;
import com.wikicivi.widget.Text_Rubbler;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;

public class ProverbsListViewHolder extends CardHolder {


    @ViewInject(R.id.proverb_title)
    public TextView tvTitle;
    @ViewInject(R.id.proverb_header)
    public TextView tvHeader;
    @ViewInject(R.id.scratch_card_text)
    public ScratchCardView scratchCardView;
    @ViewInject(R.id.rubbler)
    public Text_Rubbler rubbler;

    public ProverbsListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);

        ProverbsListViewHolder holder = (ProverbsListViewHolder) cardHolder;
        JSONObject jsonObject = new JSONObject(jsonStr);
        holder.tvTitle.setText(Html.fromHtml(jsonObject.getString("title")));
        JSONObject content=new JSONObject(jsonObject.getString("content"));
        holder.tvHeader.setText(content.getString("head"));
        holder.scratchCardView.setText(content.getString("tail"));
        holder.rubbler.beginRubbler(0xFFFF0000, 30, 1f);
    }

    @Override
    public int setLayoutFile() {
        return R.layout.proverb_tv_item;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}