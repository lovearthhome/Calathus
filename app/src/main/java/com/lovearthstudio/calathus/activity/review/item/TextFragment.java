package com.lovearthstudio.calathus.activity.review.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.fragment.BaseFragment;
import com.lovearthstudio.calathus.widget.cardview.TextCard;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;


/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_text)
public class TextFragment extends BaseFragment {

    private JSONObject data;

    @ViewInject(R.id.text_card)
    private TextCard text_card;

    public TextFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try{
                data = new JSONObject(getArguments().getString("json"));
            }catch (JSONException e){
                e.printStackTrace();

            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (data != null)
                text_card.parseData(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建Fragment的实例
     *
     * @param json
     * @return
     */
    public static TextFragment newInstance(String json) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString("json", json);
        fragment.setArguments(args);
        return fragment;
    }
}
