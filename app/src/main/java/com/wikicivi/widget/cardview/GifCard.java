package com.wikicivi.widget.cardview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kymjs.gallery.KJGalleryActivity;
import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.util.LogUtil;
import com.zky.articleproj.R;
import com.wikicivi.constant.Constant;
import com.wikicivi.net.glideprogress.ProgressListener;
import com.wikicivi.net.glideprogress.ProgressResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

/**
 * 图片的卡片布局
 * Created by zhaoliang on 16/5/12.
 */
public class GifCard extends BaseCardView implements View.OnClickListener {
    private static final String TAG = "========" + GifCard.class.getName();

    private LinearLayout ll_gif_title;
    private TextView tv_gif_title;
    private ProgressBar pb_progress;
    private TextView tv_progress;
    private GifImageView iv_gif;

    private Context context;


    private static final int UPDATE_PROGRESS = 1;
    private static String[] imageurl = {"file:///android_asset/test/image1.jpg", "file:///android_asset/test/image2.jpg", "file:///android_asset/test/image3.jpg", "file:///android_asset/test/image4.jpg",
            "file:///android_asset/test/image5.jpg", "file:///android_asset/test/image6.jpg", "file:///android_asset/test/image7.jpg", "file:///android_asset/test/image8.jpg"};

    private String gif_url = "";
    private String img_src="";
    private int img_width;
    private int img_height;
    private String img_type;
    private int img_size;
    private int img_duration;

    private OkHttpClient mOkHttpClient;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    tv_progress.setText(pb_progress.getProgress() / pb_progress.getMax() + "%");
                    break;
            }
        }
    };

    public GifCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_gif, this, true);
    }

    @Override
    public void findView() {
        ll_gif_title=(LinearLayout)findViewById(R.id.ll_gif_title);
        tv_gif_title = (TextView) findViewById(R.id.tv_gif_title);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        iv_gif = (GifImageView) findViewById(R.id.iv_gif);
    }

    @Override
    public void setOnClickListener() {
        iv_gif.setOnClickListener(this);
    }

    @Override
    public void parseData(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
            /*
            内容信息
            */
        // tvTitle.setText(jsonObject.getString("title"));

        String content_str = jsonObject.optString("content");

        JSONObject content_obj = new JSONObject(content_str);

        String art_brief = content_obj.optString("brief");
        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String gif_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);

        pb_progress.setProgress(0);
//        iv_gif.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.image_default)));
//        iv_gif.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gif_defualt));


        img_src = img_file.optString("src");
        img_height = img_file.optInt("height");
        img_width = img_file.optInt("width");
        img_type = img_file.optString("type");
        img_size = img_file.optInt("size");


        float ratio = (float) Constant.screenwith / (float) img_width;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (ratio * img_height));
        iv_gif.setLayoutParams(layoutParams);

        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                pb_progress.setMax((int) contentLength);
                pb_progress.setProgress((int) bytesRead);
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

        gif_url = Constant.baseFileUrl + img_src;
        final long  pmcBeginTime = Calendar.getInstance().getTimeInMillis();
        Glide.with(context)
//                .load("http://ww2.sinaimg.cn/large/85cccab3tw1esjq9r0pcpg20d3086qtr.jpg")
                .load(gif_url)
                .placeholder(R.mipmap.head)
                //.error(R.mipmap.ic_launcher)
                .override((int) (Constant.screenwith - Constant.mainItemPadding - Constant.mainPadding), (int) (img_height * ratio))
                // Disabling cache to see download progress with every app load
                // You may want to enable caching again in production
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        long pmcOverTime = Calendar.getInstance().getTimeInMillis();
                        Dua.getInstance().setAppPmc("GetGif",img_size/(50*1024),"50kb",pmcOverTime - pmcBeginTime,"ms");
                        return false;
                    }
                })
                .into(iv_gif);

        if ("null".equals(gif_title) || TextUtils.isEmpty(gif_title)) {
            ll_gif_title.setVisibility(View.GONE);
        } else {
            tv_gif_title.setText(gif_title);
        }
    }

    @Override
    public void onClick(View v) {
        String[] urls = new String[1];
        urls[0] = gif_url;
        KJGalleryActivity.toGallery(context, urls);
    }
}
