package com.zky.articleproj.app;

import android.app.Application;

import org.xutils.x;

/**
 * Created by pro on 16/3/11.
 */
public class ArticleAppliction extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initXutils();


    }

    /**
     * init xutils
     */
    private void initXutils() {
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
