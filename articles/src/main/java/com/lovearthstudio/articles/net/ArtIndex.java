package com.lovearthstudio.articles.net;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Author：Mingyu Yi on 2016/4/30 16:56
 * Email：461072496@qq.com
 */
public class ArtIndex extends RealmObject {
    //主键,realm现在不支持自增字段
    //@PrimaryKey
    //private int inc;

    //频道名字
    @Required
    private String channel;

    //文章的id
    @PrimaryKey
    private long tid;

    public ArtIndex() { }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    //文章根节点

    private long rid;

    public long getRid() {
        return rid;
    }

    public void setRid(long  rid) {
        this.rid = rid;
    }

}
