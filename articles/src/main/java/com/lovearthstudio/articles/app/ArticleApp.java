package com.lovearthstudio.articles.app;

import android.app.Application;

import com.lovearthstudio.articles.net.ArtModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Author：Mingyu Yi on 2016/5/2 15:09
 * Email：461072496@qq.com
 */
public class ArticleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).setModules(new ArtModule()).build();
        Realm.setDefaultConfiguration(config);
    }
}
