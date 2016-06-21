package com.lovearthstudio.calathus.widget.cardview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.lovearthstudio.calathus.R;
import com.lovearthstudio.calathus.constant.Constant;
import com.lovearthstudio.calathus.net.glideprogress.ProgressListener;
import com.lovearthstudio.calathus.net.glideprogress.ProgressResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 图片的卡片布局
 * Created by zhaoliang on 16/5/12.
 */
public class ImageCard extends BaseCardView implements View.OnClickListener {
    private static final String TAG = "========" + ImageCard.class.getName();


    private ProgressBar pb_progress;
    private TextView tv_progress;
    private ImageView iv_img;

    private Context context;
    private TextView tv_title;
    private TextView tv_brief;
    private LinearLayout layout_titleframe;
    private LinearLayout layout_briefframe;

    private static final int UPDATE_PROGRESS = 1;
    private static String[] imageurl = {"file:///android_asset/test/image1.jpg", "file:///android_asset/test/image2.jpg", "file:///android_asset/test/image3.jpg", "file:///android_asset/test/image4.jpg",
            "file:///android_asset/test/image5.jpg", "file:///android_asset/test/image6.jpg", "file:///android_asset/test/image7.jpg", "file:///android_asset/test/image8.jpg"};

    private String image_url = "";
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

    @Event(R.id.iv_img)
    private void click(View view) {
        String[] urls = new String[1];
        urls[0] = image_url;
        KJGalleryActivity.toGallery(context, urls);
    }

    public ImageCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void inflaterLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_image, this, true);
    }

    @Override
    public void findView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_brief = (TextView) findViewById(R.id.tv_brief);
        layout_titleframe = (LinearLayout) findViewById(R.id.layout_titleframe);
        layout_briefframe = (LinearLayout) findViewById(R.id.layout_briefframe);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        iv_img = (ImageView) findViewById(R.id.iv_img);

    }

    @Override
    public void setOnClickListener() {
        iv_img.setOnClickListener(this);
    }

    @Override
    public void parseData(JSONObject jsonObject) throws JSONException {
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
        String img_title = file0.optString("title");
        JSONArray img_farray = file0.optJSONArray("farray");
        JSONObject img_file = img_farray.getJSONObject(0);
        String img_src = img_file.optString("src");

        img_height = img_file.optInt("height");
        img_width = img_file.optInt("width");
        // img_type = img_file.optString("type");
        img_size = img_file.optInt("size");


        // FIXME: 计算图片的宽度和高度,有待优化!
        float ratio = ((float) (Constant.screenwith - Constant.mainPadding * 2 - Constant.mainItemPadding * 2) / (float) img_width);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (img_height * ratio));
        iv_img.setLayoutParams(params);


        //System.out.println("$$$$" + iv_img.getLayoutParams().width + ":" + iv_img.getLayoutParams().height);

        image_url = Constant.baseFileUrl + img_src;

        //System.out.println("-----------------" + image_url);

        //Picasso.with(context).load("http://img3.imgtn.bdimg.com/it/u=3350331176,1143185189&fm=21&gp=0.jpg").resize((int) (Constant.screenwith - Constant.mainPadding * 2 - Constant.mainItemPadding * 2), (int) (img_height * ratio)).into(holder.iv_img);
        //https://futurestud.io/blog/glide-image-resizing-scaling
        //FIXME: 目前还是可参考网页上的说法,如果预先知道图片大小的话,可以使用override来重写显示图片的大小
        //FIXME: 但我相信,应该可以通过类似centercrop或者fit的方式自动让图片matchparent
        //FIXME: http://stackoverflow.com/questions/35759900/glide-sizing-the-image-loaded-to-an-imageview-incorrectly-when-using-9-patch-pla
        //FIXME: 从上面链接得知,glide加载的时候根本不知道所加载img的大小,只有加载完成了才知道,所以你一定要事先告知他image_view的size.
        /*Glide.with(context)
                .load(image_url)
                .override((int)(Constant.screenwith - Constant.mainItemPadding - Constant.mainPadding), (int) (img_height * ratio))
                .fitCenter()
                .into(iv_img);*/

        //Glide.get(context).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));

        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                //System.out.println("==============" + bytesRead);
                //System.out.println("==============" + contentLength);
                //System.out.println("==============" + done);
                System.out.format("==============" + "%d%% done\n", (100 * bytesRead) / contentLength);
                pb_progress.setMax((int) contentLength);
                pb_progress.setProgress((int) bytesRead);
                mHandler.sendEmptyMessage(UPDATE_PROGRESS);
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

        /***
         * You return true if want to handle things like animations yourself and false if want glide to handle them for you.
         * 用listener就可以获取glide的各个事件
         */
        final long  pmcBeginTime = Calendar.getInstance().getTimeInMillis();
        Glide.with(context)
                // .load("http://pic36.nipic.com/20131217/6704106_233034463381_2.jpg")
                .load(Constant.baseFileUrl + img_src)
                //.placeholder(R.mipmap.head)
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
                        Dua.getInstance().setAppPmc("GetImage",img_size/(50*1024),"50kb",pmcOverTime - pmcBeginTime,"ms");
                        return false;
                    }
                })
                .into(iv_img);


        //这个时候,还没有加载成功,此时,iv_img的高宽都是原布局的高宽,如果原holder是刚刚创建的,
        //那么读出来是(0,0)否则是原图片.
        Log.e("$$$$$$", image_url + " " + iv_img.getWidth() + " " + iv_img.getHeight());
    }

    @Override
    public void onClick(View v) {
        String[] urls = new String[1];
        urls[0] = image_url;
        KJGalleryActivity.toGallery(context, urls);
    }
}
