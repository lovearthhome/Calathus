package com.wikicivi.widget.cardview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.kymjs.gallery.KJGalleryActivity;
import com.wikicivi.constant.Constant;
import com.wikicivi.widget.circleprogressbar.RateTextCircularProgressBar;
import com.zky.articleproj.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 图片的卡片布局
 * Created by zhaoliang on 16/5/12.
 */
public class GifCard extends BaseCardView implements View.OnClickListener {
    private static final String TAG = "========" + GifCard.class.getName();

    private LinearLayout ll_gif_title;
    private TextView tv_gif_title;
    // private GifImageView iv_gif;

    private SimpleDraweeView giv_view;
//    private TextView tv_bottom_click_look_bigimage;
     private LinearLayout ll_bootom_click_show_bigimage;

    private RateTextCircularProgressBar rate_progress_bar;

    private Boolean finalImageSet;


    private Context context;

    private TextView tv_title;
    private TextView tv_brief;
    private LinearLayout layout_titleframe;
    private LinearLayout layout_briefframe;

    private static final int UPDATE_PROGRESS = 1;
    private static String[] imageurl = {"file:///android_asset/test/image1.jpg", "file:///android_asset/test/image2.jpg", "file:///android_asset/test/image3.jpg", "file:///android_asset/test/image4.jpg",
            "file:///android_asset/test/image5.jpg", "file:///android_asset/test/image6.jpg", "file:///android_asset/test/image7.jpg", "file:///android_asset/test/image8.jpg"};

    private String gif_url = "";
    private String img_src = "";
    private String img_srcbak = "";
    private int img_width;
    private int img_height;
    private String img_type;
    private int img_size;
    private int img_duration;

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
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_brief = (TextView) findViewById(R.id.tv_brief);
        layout_titleframe = (LinearLayout) findViewById(R.id.layout_titleframe);
        layout_briefframe = (LinearLayout) findViewById(R.id.layout_briefframe);

        // iv_gif = (GifImageView) findViewById(R.id.iv_gif);
        giv_view = (SimpleDraweeView) findViewById(R.id.gif_view);

        GenericDraweeHierarchy hierarchy = giv_view.getHierarchy();
        hierarchy.setProgressBarImage(new CustomProgressBar());
        giv_view.setHierarchy(hierarchy);

        rate_progress_bar = (RateTextCircularProgressBar) findViewById(R.id.rate_progress_bar);
        rate_progress_bar.setMax(100);
        rate_progress_bar.getCircularProgressBar().setCircleWidth(40);
        rate_progress_bar.getCircularProgressBar().setBackgroundColor(Color.argb(255,232,232,232));//百思不得姐进度条背景色
        rate_progress_bar.getCircularProgressBar().setPrimaryColor(Color.argb(255,217,217,217));//百思不得姐进度条前景色
        rate_progress_bar.setTextColor(Color.argb(255,217,217,217));

        ll_bootom_click_show_bigimage = (LinearLayout) findViewById(R.id.ll_bootom_click_show_bigimage);
    }

    @Override
    public void setOnClickListener() {
        //iv_gif.setOnClickListener(this);
    }

    @Override
    public void parseData(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        rate_progress_bar.setVisibility(VISIBLE);
        finalImageSet = false;
        ll_bootom_click_show_bigimage.setVisibility(INVISIBLE);
        /*
        内容信息
        */
        String content_str = jsonObject.optString("content");
        JSONObject content_obj = new JSONObject(content_str);

        String title = content_obj.getString("title");
        if(title == null || title.equals("")  )
        {
            layout_titleframe.setVisibility(View.GONE);
        }else{
            tv_title.setText(title);
        }

        String brief = content_obj.getString("brief");
        if(brief == null || brief.equals("")  )
        {
            layout_titleframe.setVisibility(View.GONE);
        }else{
            tv_brief.setText(brief);
        }

        JSONArray art_files = content_obj.optJSONArray("files");

        JSONObject file0 = art_files.getJSONObject(0);
        String gif_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);

//        iv_gif.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.image_default)));
//        iv_gif.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gif_defualt));


        img_src = img_file.optString("src");
        img_srcbak = img_file.optString("srcbak");
        img_height = img_file.optInt("height");
        img_width = img_file.optInt("width");
        img_type = img_file.optString("type");
        img_size = img_file.optInt("size");


        float ratio = (float) Constant.screenwith / (float) img_width;
        int height = (int) (ratio * img_height);
        if (height > 1500) {
            height = 700;
            ll_bootom_click_show_bigimage.setVisibility(VISIBLE);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, height);
        giv_view.setLayoutParams(layoutParams);

       /* final ProgressListener progressListener = new ProgressListener() {
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

        final long pmcBeginTime = Calendar.getInstance().getTimeInMillis();*/
        gif_url = Constant.baseFileUrl + img_src;
        //gif_url = img_srcbak;
        System.out.println("==============:+++++++++++++" + gif_url);
       /* Glide.with(context)
//                .load("http://ww2.sinaimg.cn/large/85cccab3tw1esjq9r0pcpg20d3086qtr.jpg")
                .load(gif_url)
                .placeholder(R.mipmap.head)
//                .error(R.mipmap.ic_launcher)
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
                        Dua.getInstance().setAppPmc("GetGif", img_size / (50 * 1024), "50kb", pmcOverTime - pmcBeginTime, "ms");
                        return false;
                    }
                })
                .into(iv_gif);
                */
        //Glide.with(context).load(gif_url).into(iv_gif);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(gif_url))
                .setAutoPlayAnimations(true)
                .setControllerListener(controllerListener)
                // other setters
                .build();
        giv_view.setController(controller);

    }

    @Override
    public void onClick(View v) {
        String[] urls = new String[1];
        urls[0] = gif_url;
        KJGalleryActivity.toGallery(context, urls);
    }

    class CustomProgressBar extends Drawable {

        @Override
        public void draw(Canvas canvas) {

        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }

        @Override
        protected boolean onLevelChange(int level) {
            // level is on a scale of 0-10,000
            // where 10,000 means fully downloaded

            // your app's logic to change the drawable's
            // appearance here based on progress
            rate_progress_bar.setProgress(level/20);
            System.out.println("--------:" + level);
            if(level >= 10000)
            {
                MyAsyncTask pbtask = new MyAsyncTask();
                pbtask.execute();
            }
            return false;
        }
    }

    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(
                String id,
                @Nullable ImageInfo imageInfo,
                @Nullable Animatable anim) {
            if (imageInfo == null) {
                return;
            }
            QualityInfo qualityInfo = imageInfo.getQualityInfo();
            FLog.d("Final image received! " +
                            "Size %d x %d",
                    "Quality level %d, good enough: %s, full quality: %s",
                    imageInfo.getWidth(),
                    imageInfo.getHeight(),
                    qualityInfo.getQuality(),
                    qualityInfo.isOfGoodEnoughQuality(),
                    qualityInfo.isOfFullQuality());

            //rate_progress_bar.setVisibility(INVISIBLE);
            finalImageSet = true;
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            //FLog.d("Intermediate image received");
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            FLog.e(getClass(), throwable, "Error loading %s", id);
        }
    };

    /**
     * 我们假设GIF渲染的过程是个必不可少的环节，这个环节必然会损失时间，这个过程必须要模拟
     * 我们先假设渲染时间是下载时间相等
     *
     *
     */
    class MyAsyncTask extends AsyncTask<Void,Integer,Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // 这个方法是在执行异步任务之前的时候执行，并且是在UI Thread当中执行的，通常我们在这个方法里做一些UI控件的初始化的操作，
            super.onPreExecute();
            rate_progress_bar.setProgress(50);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate();
            rate_progress_bar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            float timePast = 5.0f;//假设过去了5秒
            float timeLeft = 5.0f;//假设剩下五秒就会渲染成功，那么总时间就是10秒
            while(true)
            {
                try
                {
                    Thread.sleep(100);
                    timeLeft -= 0.1f;
                    timePast += 0.1f;
                    if(timeLeft < 2.5f && finalImageSet == false)
                    {
                        timeLeft += 1.0f;
                    }
                    if(timeLeft <0.0f)
                        break;
                }catch (Exception e){
                    e.printStackTrace();
                }
                publishProgress((int)(timePast/(timeLeft+timePast)*100));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            rate_progress_bar.setVisibility(INVISIBLE);
        }

    }


}
