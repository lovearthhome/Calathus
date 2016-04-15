package com.zky.articleproj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zky.articleproj.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import uk.co.senab.photoview.PhotoViewAttacher;

@ContentView(R.layout.activity_image_zoom)
public class ImageZoomActivity extends Activity {

    @ViewInject(R.id.image_zoom)
    private ImageView image_zoom;
    PhotoViewAttacher mAttacher;

    @Event(R.id.btn_back)
    private void click(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x.view().inject(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Picasso.with(this).load(getIntent().getStringExtra("image_url")).into(image_zoom);
        mAttacher = new PhotoViewAttacher(image_zoom);
    }
}