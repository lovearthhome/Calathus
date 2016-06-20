package com.lovearthstudio.calathus.activity.guide.guidepages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.VideoView;

import com.lovearthstudio.calathus.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;


/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_guide8)
public class GuideFragment8 extends GuideFragment {

    @ViewInject(R.id.video_view)
    private VideoView video_view;

    public GuideFragment8() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println(getContext().getPackageName());
        video_view.setVideoPath("android.resource://com.lovearthstudio.calathus" + "/" + R.raw.first_film);
    }

    @Override
    protected void lazyLoad() {
        video_view.start();
        System.out.println("--------------------:l" +
                "azyLoad");
    }

    @Override
    protected void lazyExit() {
        if (video_view != null)
            video_view.pause();
        System.out.println("--------------------:lazyExit");
    }
}
