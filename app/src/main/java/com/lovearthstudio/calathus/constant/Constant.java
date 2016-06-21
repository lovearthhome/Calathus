package com.lovearthstudio.calathus.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量类
 * 定义项目基本的常量地址
 * Created by pro on 16/2/23.
 */
public class Constant {
    /*用户*/
    public static final String userUrl = "http://api.xdua.org/users";
    /* 项目基本地址 */
    public static final String baseUrl = "http://api.wikicivi.com/articles";
    /* 项目图片基本地址 */
    public static final String baseFileUrl = "http://files.wikicivi.com/files/";
    /*用户头像基本地址*/
    public static final String baseAvatarUrl = "http://files.xdua.org/files/avatar/";
    /*用户头像默认地址*/
    public static final String defaultAvatarUrl = "http://files.xdua.org/files/avatar/anonymous.png";
    /* dua_id */
    public static long dua_id = 41;

    public static List<String> rules=new ArrayList<>();

    /* 屏幕宽高 */
    public static int screenwith;
    public static int screenheight;

    /* 主页面左右边距 */
    public static float mainPadding;
    public static float mainItemPadding;
}
