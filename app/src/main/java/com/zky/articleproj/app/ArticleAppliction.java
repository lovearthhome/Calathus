package com.zky.articleproj.app;

import android.app.Application;


import com.lovearthstudio.duasdk.Dua;

import org.xutils.x;


/**
 * Created by pro on 16/3/11.
 */
public class ArticleAppliction extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initXutils();
        /**
         * 为保证使用dua的时候dua已经初始化了，在这个地方init一下。
        * */
        Dua.init(this);
    }

    /**
     * init xutils
     */
    private void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
