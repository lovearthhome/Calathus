package com.lovearthstudio.articles.net;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Author：Mingyu Yi on 2016/4/30 16:56
 * Email：461072496@qq.com
 */
public class ArtViewBlock extends RealmObject {
    //主键,realm现在不支持自增字段
    //@PrimaryKey
    //private int inc;

    //频道名字
    @PrimaryKey
    private String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }



    //文章的tid的min
    private long tidmin;

    public long getTidmin() {
        return tidmin;
    }

    public void setTidmin(long tidmin) {
        this.tidmin = tidmin;
    }
    //文章的tid的min
    private long tidmax;

    public long getTidmax() {
        return tidmax;
    }

    public void setTidmax(long tidmax) {
        this.tidmax = tidmax;
    }
    /*
    //文章的tid的min
    private long tmpmin;

    public long getTmpmin() {
        return tmpmin;
    }

    public void setTmpmin(long tmpmin) {
        this.tmpmin = tmpmin;
    }
    //文章的tid的min
    private long tmpmax;

    public long getTmpmax() {
        return tmpmax;
    }

    public void setTmpmax(long tmpmax) {
        this.tmpmax = tmpmax;
    }
    */
    public ArtViewBlock() { }





}
