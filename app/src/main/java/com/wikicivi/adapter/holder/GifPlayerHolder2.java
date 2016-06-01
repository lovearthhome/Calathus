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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.zky.articleproj.R;
import com.wikicivi.adapter.holder.base.BaseHolder;
import com.wikicivi.adapter.holder.base.CardHolder;
import com.wikicivi.cache.CacheManager;
import com.wikicivi.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.InputSource;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class GifPlayerHolder2 extends CardHolder {

    @ViewInject(R.id.tv_music_title)
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

    private OkHttpClient mOkHttpClient;
    private final static String DOWNLOAD_URL = "https://i.imgur.com/mYBXl6X.jpg";
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

    public GifPlayerHolder2(View itemView) {
        super(itemView);

        client = new AsyncHttpClient();
        responseHandler = new GifHandler();
        System.out.println("--------------gif:born");
    }

    @Override
    public void bindView(Context context, BaseHolder cardHolder, String jsonStr) throws JSONException {
        GifPlayerHolder2 holder = (GifPlayerHolder2) cardHolder;


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


        final ProgressListener progressListener = new ProgressListener() {

            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                int progress = (int) ((100 * bytesRead) / contentLength);

                // Enable if you want to see the progress with logcat
                // Log.v(LOG_TAG, "Progress: " + progress + "%");
                pb_gif.setProgress(progress);

//                    Message obtain = Message.obtain();
//                    obtain.what = UPDATE_TV_PROGRESS;
//                    obtain.obj = progress;
//                    mHandler.sendMessage(obtain);

            }
        };

        mOkHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        }).build();


        Glide.get(context)
                .register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));

        Glide.with(context)
                .load("http://img3.imgtn.bdimg.com/it/u=2908161623,947886430&fm=21&gp=0.jpg")
                // Disabling cache to see download progress with every app load
                // You may want to enable caching again in production
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_gif);

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

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}
