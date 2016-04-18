package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.cache.CacheManager;
import com.zky.articleproj.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import io.vov.vitamio.utils.Log;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.InputSource;

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

    private boolean loading = false;
    private boolean playable = false;
    /*
    *  0:  创建出来,干净的,没有沾染过,等用户点击后就进入加载状态/
    *  1:  用户点击了,正在加载,但是没有加载成功
    *  2:  用户加载成功结束后,正在播放的状态/或者加载失败后
    *  3:  表明这是个回收的holder,此时间它的drawable是有值的.
    * */

    private GifHandler responseHandler;
    private RequestHandle requestHandle;
    private InputSource.ByteArraySource inputSource;

    @Event(R.id.iv_gif)
    private void click(View view) {
        if (playable == false) {
            if (loading) {
                loading = false;
                Toast.makeText(context, "已经停止加载", Toast.LENGTH_SHORT).show();
                if (requestHandle != null && !requestHandle.isFinished()) {
                    requestHandle.cancel(true);
                }

            } else {

                requestHandle = client.get(Constant.baseFileUrl + img_src, responseHandler);
                loading = true;
                Toast.makeText(context, "开始奋力加载", Toast.LENGTH_SHORT).show();
            }

        }
        if (playable == true) {
            if (drawable.isPlaying()) {
                drawable.stop();
            } else {
                drawable.start();
            }
        }

    }

    public GifPlayerHolder(View itemView) {
        super(itemView);

        client = new AsyncHttpClient();
        responseHandler = new GifHandler();
        System.out.println("--------------gif:born");
    }

    @Override
    public void bindView(Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {
        GifPlayerHolder holder = (GifPlayerHolder) baseHolder;


        JSONObject jsonObject = new JSONObject(jsonStr);

        String content_str = jsonObject.optString("content");
        JSONObject content_obj = new JSONObject(content_str);
        String title = content_obj.optString("title");
        String art_brief = content_obj.optString("brief");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String img_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);

        String new_src = img_file.optString("src");


        if ("null".equals(title) || TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }


        if (new_src.equals(img_src) && img_src != null) {
            Log.e("What fuck", "what fuck");
            // iv_gif.setInputSource(inputSource);
            return;

        }

        //这个时候,本gifhoulder可能是回收的的gifholder,它上面
        // 1: 它里面的asynchttpclient正在下载内容,这个时候应该关闭这个client链接
        // 2: 它已经下载完了,正在播放,这个时候,应该把旧的这个gifview的绘图区域给清空

        //如果是回收的holder,并且正在请求,那么关闭它
        if (requestHandle != null && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
            loading = false;
        }

        //如果drawable不为空,那么我就强制回收,此时我也不管它能不能回收.FIXME,如果此时,它还没下载成功,它什么东西也没有,我就不能回收.这个地方的判断要完善
        //java.lang.RuntimeException: Canvas: trying to use a recycled bitmap android.graphics.Bitmap@20c2fcb4

/*        if (drawable != null && !drawable.isRecycled()) {
            // drawable.recycle();
            //drawable.stop();
            drawable.setVisible(false, false);

        }*/
        //给进度条设置为0
        pb_gif.setProgress(0);

        iv_gif.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.image_default)));
        iv_gif.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gif_defualt));
        playable = false;

        System.out.println("what fuck " + tvTitle.getText().toString() + " old_src:" + img_src + "  new_src: " + new_src);

        img_src = img_file.optString("src");
        img_height = img_file.optInt("height");
        img_width = img_file.optInt("width");
        img_type = img_file.optString("type");
        img_size = img_file.optInt("size");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (Constant.screenwith / img_width) * img_height);

        iv_gif.setLayoutParams(layoutParams);

        //此时应该尝试去缓存读取
        byte[] fromCache = (byte[]) CacheManager.getInstance().getFromCache(img_src);
        if (fromCache != null) {
            //FIXME: 这块很快,但loading先为true,后为false;
            loading = true;
            try {
                drawable = new GifDrawable(fromCache);
            } catch (IOException e) {
                e.printStackTrace();
            }

            iv_gif.setBackgroundDrawable(drawable);
            iv_gif.setImageBitmap(null);
            playable = true;

            loading = false;
        }

    }


    @Override
    public int setLayoutFile() {
        return R.layout.gif_player_list_item;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {
        //System.out.println("--------------gif:onChildViewAttachedToWindow" + tvTitle.getText().toString());
        //在get新的gif前,把旧的清洗掉
        //这个旧的gif要么正在加载,要么加载完了,要么正在播放


        // client.get(Constant.baseFileUrl + img_src, responseHandler);
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
        //client.delete(Constant.baseFileUrl + img_src, responseHandler);
        //System.out.println("--------------gif:onChildViewDetachedFromWindow" + tvTitle.getText().toString());
    }

    /**
     * gif处理器类
     */
    class GifHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            loading = false;
            try {
                CacheManager.getInstance().addToCache(img_src, responseBody);
                drawable = new GifDrawable(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //drawable.stop();
            iv_gif.setBackgroundDrawable(drawable);
            iv_gif.setImageBitmap(null);
            playable = true;

/*            inputSource = new InputSource.ByteArraySource(responseBody);
            iv_gif.setInputSource(inputSource);*/
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
