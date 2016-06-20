package com.lovearthstudio.calathus.activity.guide.guidepages;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.lovearthstudio.calathus.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import co.mobiwise.playerview.MusicPlayerView;


/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_guide7)
public class GuideFragment7 extends GuideFragment {

    @ViewInject(R.id.mpv)
    private MusicPlayerView mpv;
    private MediaPlayer mp; //媒体播放器


    private Boolean isPlaying = false;
    public GuideFragment7() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mp = MediaPlayer.create(getContext(),R.raw.zhiyueguang);

    }

    @Override
    protected void lazyLoad() {
        mpv.start();
        mp.start();
        isPlaying = true;
    }

    /**
     *  由于实现的方式，lazyExit可能在lazyLoad或者create之前执行，所以我们需要在这个地方判断，lazyExit一定要在lazyLoad之后
     * */
    @Override
    protected void lazyExit() {
        if(isPlaying)
        {
            mpv.stop();
            mp.stop();
            isPlaying = false;
        }

    }

}
