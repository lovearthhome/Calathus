package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class GifPlayerHolder extends BaseHolder {

    @ViewInject(R.id.tv_index1_title)
    private TextView tvTitle;

    @ViewInject(R.id.iv_gif)
    private GifImageView iv_gif;
    @ViewInject(R.id.pb_gif)
    private ProgressBar pb_gif;

    GifDrawable drawable = null;

    private AsyncHttpClient client;
    private String img_src;
    private int img_width;
    private int img_height;
    private String img_type;
    private int img_size;
    private int img_duration;

    private GifHandler responseHandler;

    @Event(R.id.iv_gif)
    private void click(View view) {
        if (drawable == null) {
            Toast.makeText(context, "正在努力加载", Toast.LENGTH_SHORT).show();
        } else {
            if (drawable.isPlaying()) {
                drawable.stop();
            } else {
                drawable.start();
            }
        }
    }

    public GifPlayerHolder(View itemView, AsyncHttpClient client) {
        super(itemView);
        this.client = client;
    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        GifPlayerHolder holder = (GifPlayerHolder) baseHolder;
        JSONObject jsonObject = new JSONObject(jsonStr);

        String content_str = jsonObject.optString("content");
        JSONObject content_obj = new JSONObject(content_str);
        String title = content_obj.optString("title");
        if ("null".equals(title) || TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        String art_brief = content_obj.optString("brief");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String img_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);
        img_src = img_file.optString("src");
        img_height = img_file.optInt("height");
        img_width = img_file.optInt("width");
        img_type = img_file.optString("type");
        img_size = img_file.optInt("size");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (Constant.screenwith / img_width) * img_height);

        iv_gif.setLayoutParams(layoutParams);


        responseHandler = new GifHandler();
        client.get(Constant.baseFileUrl + img_src, responseHandler);
    }


    @Override
    public int setLayoutFile() {
        return R.layout.gif_player_list_item;
    }

    /**
     * gif处理器类
     */
    class GifHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            try {
                drawable = new GifDrawable(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawable.stop();
            iv_gif.setBackgroundDrawable(drawable);

            iv_gif.setImageBitmap(null);
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            pb_gif.setMax((int) totalSize);
            pb_gif.setProgress((int) bytesWritten);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            //FIXME: 加载失败的时候调用
        }
    }
}
