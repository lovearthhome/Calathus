package com.lovearthstudio.articles.net;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Author：Mingyu Yi on 2016/4/30 16:56
 * Email：461072496@qq.com
 */
public class ArtItem extends RealmObject {
    //主键,realm现在不支持自增字段
    //private int inc;

    //文章的id
    @PrimaryKey
    private long tid;

    public long getTid() {return tid;}
    public void setTid(long content) {this.tid = tid;}

    //文章的作者name// Name cannot be null
    @Required
    private String data;

    public String getData() {return data;}
    public void setData(String  data) {this.data = data;}

    /*
    //文章的media:Text,Video,Audio,
    @Required
    private String media;

    public String getMedia() {return media;}
    public void setMedia(String  media) {this.media = media;}
    //文章的次媒体类型，作为种类存在
    @Required
    private String cato;

    public String getCato() {return cato;}
    public void setCato(String  cato) {this.cato = cato;}
    //文章的模板类型
    //@Required
    private int tmpl;

    public int getTmpl() {return tmpl;}
    public void setTmpl(int tmpl) {this.tmpl = tmpl;}
    //文章的模板类型
    //@Required
    private int top;

    public int getTop() {return top;}
    public void setTop(int top) {this.top = top;}
    //文章的模板类型
    //@Required
    private int hot;

    public int getHot() {return hot;}
    public void setHot(int hot) {this.hot = hot;}
    //文章的点赞数目
    //@Required
    private int good;

    public int getGood() {return good;}
    public void setGood(int good) {this.good = good;}
    //文章的点踩数目
   // @Required
    private int bad;

    public int getBad() {return bad;}
    public void setBad(int bad) {this.bad = bad;}
    //文章的分享数目
    //@Required
    private int shar;

    public int getShar() {return shar;}
    public void setShar(int shar) {this.shar = shar;}
    //文章的数目
    //@Required
    private int comt;

    public int getComt() {return comt;}
    public void setComt(int comt) {this.comt = comt;}
    //文章的收藏数目
    //@Required
    private int star;

    public int getStar() {return star;}
    public void setStar(int star) {this.star = star;}
    //文章的打赏数目
    //@Required
    private int gem;

    public int getGem() {return gem;}
    public void setGem(int gem) {this.gem = gem;}

    //文章的content
    @Required
    private String content;

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    //文章的作者id
    //@Required
    private long eid;

    public long getEid() {return eid;}
    public void setEid(long eid) {this.eid = eid;}

    //文章的作者name
    @Required
    private String ename;

    public String getEname() {return ename;}
    public void setEname(String  ename) {this.ename = ename;}

    //文章的作者头像地址
    @Required
    private String avatar;
    public String getAvatar() {return avatar;}
    public void setAvatar(String  avatar) {this.ename = avatar;}

    //文章的创建时间
    @Required
    private Date ctime;

    public Date getCtime() {return ctime;}
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
    //文章的更新时间
    @Required
    private Date utime;

    public Date getUtime() {return utime;}
    public void setUtime(Date utime) {
        this.utime = utime;
    }
    */
    public ArtItem() { }

}
