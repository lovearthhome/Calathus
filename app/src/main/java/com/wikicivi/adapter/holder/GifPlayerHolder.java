package com.wikicivi.adapter.holder;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.loopj.android.http.AsyncHttpClient;
import com.zky.articleproj.R;
import com.wikicivi.adapter.holder.base.BaseHolder;
import com.wikicivi.adapter.holder.base.CardHolder;
import com.wikicivi.constant.Constant;
import com.wikicivi.net.glideprogress.ProgressListener;
import com.wikicivi.net.glideprogress.ProgressResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class GifPlayerHolder extends CardHolder {

    @ViewInject(R.id.tv_music_title)
    private TextView tvTitle;

    @ViewInject(R.id.iv_gif)
    private GifImageView iv_gif;
    @ViewInject(R.id.pb_gif)
    private ProgressBar pb_gif;

    /*  @ViewInject(R.id.loading)
      private CircleLoadingView circleLoadingView;
  */
    GifDrawable drawable = null;

    private AsyncHttpClient client;
    private String img_src;
    private int img_width;
    private int img_height;
    private String img_type;
    private int img_size;
    private int img_duration;

    public OkHttpClient mOkHttpClient;
    private final static String DOWNLOAD_URL = "https://i.imgur.com/mYBXl6X.jpg";

    @Event(R.id.iv_gif)
    private void click(View view) {

    }

    public GifPlayerHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {

        super.bindBaseView(context, (CardHolder) cardHolder, jsonStr);

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


        //这个时候,本gifhoulder可能是回收的的gifholder,它上面
        // 1: 它里面的asynchttpclient正在下载内容,这个时候应该关闭这个client链接
        // 2: 它已经下载完了,正在播放,这个时候,应该把旧的这个gifview的绘图区域给清空

        //如果是回收的holder,并且正在请求,那么关闭它


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


        /*
        System.out.println("what fuck " + tvTitle.getText().

                toString()

                + " old_src:" + img_src + "  new_src: " + new_src);
        */

        img_src = img_file.optString("src");
        img_height = img_file.optInt("height");
        img_width = img_file.optInt("width");
        img_type = img_file.optString("type");
        img_size = img_file.optInt("size");

        float ratio = (float) Constant.screenwith / (float) img_width;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (ratio * img_height));

        iv_gif.setLayoutParams(layoutParams);

        /*final ProgressResponseBody.ProgressListener progressListener = new ProgressResponseBody.ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                pb_gif.setProgress((int) (bytesRead / contentLength));
                //System.out.println("---------下载进度:" + bytesRead + ":" + contentLength);
            }
        };

        mOkHttpClient = new OkHttpClient.Builder().
                addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                .build();
                    }
                }).build();

        Glide.get(context).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));*/

        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {

                //System.out.println("gif==============" + bytesRead);
                //System.out.println("gif==============" + contentLength);
                //System.out.println("gif==============" + done);
                System.out.format("gif==============" + "%d%% done\n", (100 * bytesRead) / contentLength);
                pb_gif.setMax((int) contentLength);
                pb_gif.setProgress((int) bytesRead);
            }
        };

        mOkHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
            }
        }).build();

        Glide.get(context).register(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(mOkHttpClient));

        Log.e("gifurl", Constant.baseFileUrl + img_src);

        Glide.with(context)
                //.load("http://ww2.sinaimg.cn/large/85cccab3tw1esjq9r0pcpg20d3086qtr.jpg")
                .load(Constant.baseFileUrl + img_src)
                .placeholder(R.mipmap.head)
                //.error(R.mipmap.ic_launcher)
                .override((int) (Constant.screenwith - Constant.mainItemPadding - Constant.mainPadding), (int) (img_height * ratio))
                // Disabling cache to see download progress with every app load
                // You may want to enable caching again in production
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_gif);

        if ("null".equals(title) || TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

    }


    @Override
    public int setLayoutFile() {
        return R.layout.gif_player_list_item;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {
        ////System.out.println("--------------gif:onChildViewAttachedToWindow" + tvTitle.getText().toString());
        //在get新的gif前,把旧的清洗掉
        //这个旧的gif要么正在加载,要么加载完了,要么正在播放


        // client.get(Constant.baseFileUrl + img_src, responseHandler);
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
        //client.delete(Constant.baseFileUrl + img_src, responseHandler);
        ////System.out.println("--------------gif:onChildViewDetachedFromWindow" + tvTitle.getText().toString());
    }

}
