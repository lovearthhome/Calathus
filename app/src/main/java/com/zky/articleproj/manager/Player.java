package com.zky.articleproj.manager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import co.mobiwise.playerview.MusicPlayerView;


/*
http://bbs.csdn.net/topics/390762694
* MediaPlayer调用了prepareAsync方法后，待完成触发了OnPreparedListener的onPrepared方法后，
* 才能调用MediaPlayer的start方法，否则会报错的。
*
* 我想知道prepareAsync这个方法所需要的时间跟那些参数有关？
下面是我的应用使用这个方法的一些数据，在相同WIFI网络、相同测试手机、相同实现代码的情况下：
1、国内服务器上的mp3音频文件，音频文件长度3-30分钟，prepareAsync方法只要0-3秒就完成了，大部分是1秒。
2、英国服务器上的mp3音频文件，音频文件长度100分钟，prepareAsync方法要30-200秒才能完成了，大部分是60秒。
3、英国服务器上的直播流，音频文件长度不知道，prepareAsync方法只要10-60秒才能完成了，大部分是20秒。
*
* 下面是播放音频的重要代码：
先说一下跟其它播放不一样的地方，因为需求问题，播放器对象为单例，所以不能每换一个音频地址就释放一次播放器，只能重置。
// 停止
if (sMediaPlayer.isPlaying()) sMediaPlayer.stop();
// 重置参数
sMediaPlayer.reset();
//设置播放源
sMediaPlayer.setDataSource(MEDIA_PATH);
//开始准备
sMediaPlayer.prepareAsync();


MediaPlayer在不用的时候要用release()释放掉。
注意这里因为MediaPlayer也有pause()操作，可以暂停，所以不注意的话，会在Activity的onPause()中调用MediaPlayer.pause()，这样做是不恰当的。
在MediaPlayer#pause()中并不会释放底层占用的资源，这样别的Activity如果要用一些公共资源的话，就得不到了。正确的做法是，在不需要播放时，记录下当前的位置，并用MediaPlayer#release()释放；需要播放时再从重新初始化，并seek到记录的位置，从那里开始播放。
当然也不要那么教条，应根据你的实际需要，灵活运用。

http://blog.csdn.net/thl789/article/details/7375008
*
* */

public class Player implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener {

    private MediaPlayer mediaPlayer; // 媒体播放器
    private MusicPlayerView mpv;    //播放器的控制器
    private boolean isPreparing = false;    //播放器在准备？
    private boolean isPrepared  = false;   //播放器准备好了没？

    public Player(MusicPlayerView mpv) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);

            this.mpv=mpv;
            this.isPreparing = false;
            this.isPrepared = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void play() {
        mediaPlayer.start();
        //ib_play.setBackgroundResource(R.drawable.button_pause);
    }

    /**
     * @param url url地址. 设置player的url地址
     *
     */
    public void playUrl(String url)
    {
        try {
            //prepare就是让mediaplayer准备，准备好就播放。
            mediaPlayer.setDataSource(url); // 设置数据源
            // mediaPlayer.prepare(); // prepare自动播放
            ////提供了同步和异步两种方式设置播放器进入prepare状态，需要注意的是，如果MediaPlayer实例是由create方法创建的，那么第一次启动播放前不需要再调用prepare（）了，因为create方法里已经调用过了。
            mediaPlayer.prepareAsync();
            isPreparing = true;
            //mpv.showPreparing();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param 重置
     */
    public void reset() {
        try {
            //.reset()可以使播放器从Error状态中恢复过来，重新会到Idle状态。
            mediaPlayer.reset();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param 释放播放器的资源
     */
    public void realease(String url) {
        try {
            //FIXME:
            //release()可以释放播放器占用的资源，一旦确定不再使用播放器时应当尽早调用它释放资源。
            mediaPlayer.release();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();

    }
    public boolean isPreparing()
    {
        return isPreparing;

    }
    public boolean isPrepared()
    {
        return isPrepared;

    }

    // 暂停
    public void pause() {
        mediaPlayer.pause();
        //ib_play.setBackgroundResource(R.drawable.button_play);
    }

    // 停止
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            //FIXME: 为了性能考虑，这个地方不希望释放mediaplayer资源，而仅仅是reset
            //mediaPlayer.release();
            //mediaPlayer = null;
            mediaPlayer.reset();
            isPrepared = false;

        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //mpv.hidePreparing();
        isPreparing = false;
        isPrepared = true;
        mp.start();
        mpv.start();
        mpv.setProgress(0);
        //FIXME:这个时候的获取的时常非常大，不知道为什么
        //mpv.setMax(mp.getDuration());
        Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
    }

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //seekBar.setSecondaryProgress(percent);
        /*int currentProgress = seekBar.getMax()
                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Log.e(currentProgress + "% play", percent + " buffer");*/

        System.out.println("加载进度:" + percent + "%");
        mpv.setLoadingProgress(percent);
    }


}
