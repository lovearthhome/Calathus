package com.zky.articleproj.adapter.holder.zhaoliang;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zky.articleproj.R;
import com.zky.articleproj.adapter.holder.base.BaseHolder;
import com.zky.articleproj.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhaoliang on 16/4/7.
 */
public class ImageViewHolder extends BaseHolder {

    private static String[] imageurl = {"file:///android_asset/test/image1.jpg", "file:///android_asset/test/image2.jpg", "file:///android_asset/test/image3.jpg", "file:///android_asset/test/image4.jpg",
            "file:///android_asset/test/image5.jpg", "file:///android_asset/test/image6.jpg", "file:///android_asset/test/image7.jpg", "file:///android_asset/test/image8.jpg"};

    @ViewInject(R.id.tv_index1_title)
    public TextView tvTitle;

    @ViewInject(R.id.iv_img)
    public ImageView iv_img;

    private String image_url = "";
    private int img_width;
    private int img_height;
    private String img_type;
    private int img_size;
    private int img_duration;

    @Event(R.id.iv_img)
    private void click(View view) {
        Intent intent = new Intent("com.zky.articleproj.activity.ImageZoomActivity");
        intent.putExtra("image_url", image_url);
        context.startActivity(intent);
    }

    public ImageViewHolder(View itemView) {
        super(itemView);
    }


    /*这个bindView实质是android的recycleview触发的,这里的baseholer实质是指向各个子类的基类引用,
    * 这个baseholder有可能是刚创建的
    * 有可能是回收的.如果是回收的,上面的视频啊,图片啊,gif啊,还在,并且还可以播放.
    * 为了避免这种情况,我们需要对holder进行清理.
    * */
    @Override
    public void bindView(final Context context, BaseHolder baseHolder, String jsonStr) throws JSONException {

        final ImageViewHolder holder = (ImageViewHolder) baseHolder;
        Log.e("$$$$", holder.image_url + " " + holder.iv_img.getWidth() + " " + holder.iv_img.getHeight());


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


        System.out.println("$$$$" + iv_img.getLayoutParams().width + ":" + iv_img.getLayoutParams().height);

        image_url = Constant.baseFileUrl + img_src;

        System.out.println("-----------------" + image_url);

        //Picasso.with(context).load("http://img3.imgtn.bdimg.com/it/u=3350331176,1143185189&fm=21&gp=0.jpg").resize((int) (Constant.screenwith - Constant.mainPadding * 2 - Constant.mainItemPadding * 2), (int) (img_height * ratio)).into(holder.iv_img);
        //https://futurestud.io/blog/glide-image-resizing-scaling
        //FIXME: 目前还是可参考网页上的说法,如果预先知道图片大小的话,可以使用override来重写显示图片的大小
        //FIXME: 但我相信,应该可以通过类似centercrop或者fit的方式自动让图片matchparent
        //FIXME: http://stackoverflow.com/questions/35759900/glide-sizing-the-image-loaded-to-an-imageview-incorrectly-when-using-9-patch-pla
        //FIXME: 从上面链接得知,glide加载的时候根本不知道所加载img的大小,只有加载完成了才知道,所以你一定要事先告知他image_view的size.
        Glide.with(context)
                .load(image_url)
                .override((int)(Constant.screenwith - Constant.mainItemPadding - Constant.mainPadding), (int) (img_height * ratio))
                .fitCenter()
                .into(iv_img);


        //这个时候,还没有加载成功,此时,iv_img的高宽都是原布局的高宽,如果原holder是刚刚创建的,
        //那么读出来是(0,0)否则是原图片.
        Log.e("$$$$$$", image_url + " " + iv_img.getWidth() + " " + iv_img.getHeight());

    }

    @Override
    public int setLayoutFile() {
        return R.layout.image_view_holder;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
