package com.wikicivi.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovearthstudio.articles.service.ArticleService;
import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duasdk.ui.DuaActivityLogin;
import com.lovearthstudio.duasdk.ui.DuaActivityProfile;
import com.lovearthstudio.duasdk.util.LogUtil;
import com.zky.articleproj.R;
import com.wikicivi.activity.menu.About_Activity;
import com.wikicivi.activity.menu.followee.FolloweeActivity;
import com.wikicivi.activity.menu.follower.FollowerActivity;
import com.wikicivi.activity.menu.settings.SettingActivity;
import com.wikicivi.activity.review.ReviewActivity;
import com.wikicivi.constant.Constant;
import com.wikicivi.fragment.IndexFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@SuppressLint("NewApi")
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    public static final int DUA_LOGIN_REQUEST_CODE=10086;
    private static final int INIT_SUCCESS = 1;
    @ViewInject(R.id.tool_bar)
    private Toolbar mToolbar;
    @ViewInject(R.id.tab_layout)
    private TabLayout mTabLayout;
    @ViewInject(R.id.view_pager)
    private ViewPager mViewPager;
    @ViewInject(R.id.draw_layout)
    private DrawerLayout mDrawerLayout;
    @ViewInject(R.id.nav_view)
    private NavigationView mNavigationView;

    private View headView;

    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView user_icon;
    private TextView userName;
    private MenuItem reviewItem;
    private RomoteServiceConnection ArticleServiceConnection;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_SUCCESS:
                    MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(adapter);
                    //修改ViewPager的缓存页面数量
                    //viewpager当前页面两侧缓存/预加载的页面数目。当页面切换时，当前页面相邻两侧的numbers页面不会被销毁。

                    mViewPager.setOffscreenPageLimit(2);
                    mTabLayout.setupWithViewPager(mViewPager);
                    break;
            }
        }
    };

    public Dua dua;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArticleServiceConnection = new RomoteServiceConnection();
        bindService(new Intent(this, ArticleService.class), ArticleServiceConnection, Context.BIND_AUTO_CREATE);

        getConstant();
        dua=Dua.init(getApplicationContext());
        headView = mNavigationView.getHeaderView(0);
        user_icon = (ImageView) headView.findViewById(R.id.user_icon);
        userName=(TextView)headView.findViewById(R.id.textView_user_name);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("马拉松");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);



        reviewItem=mNavigationView.getMenu().findItem(R.id.nav_review);
        reviewItem.setVisible(false);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(dua.getCurrentDuaUser().logon){
                    switch (item.getItemId()) {
                        case R.id.nav_settings:
                            startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            break;
                        case R.id.nav_about:
                            startActivity(new Intent(MainActivity.this, About_Activity.class));
                            break;
                        case R.id.nav_fans:
                            startActivity(new Intent(MainActivity.this, FollowerActivity.class));
                            break;
                        case R.id.nav_following:
                            startActivity(new Intent(MainActivity.this, FolloweeActivity.class));
                            break;
                        case R.id.nav_review:
                            startActivity(new Intent(MainActivity.this, ReviewActivity.class));
                            break;
                    }
                }else {
                    switch (item.getItemId()){
                        case R.id.nav_settings:
                            startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            break;
                        case R.id.nav_about:
                            startActivity(new Intent(MainActivity.this, About_Activity.class));
                            break;
                        default:
                            goLogin();
                    }

                }
                return true;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==DUA_LOGIN_REQUEST_CODE&&data!=null){
            Bundle extra=data.getExtras();
            if(extra!=null){
                String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* 获取一下常量参数 */
    private void getConstant() {
        WindowManager wm = this.getWindowManager();
        /* 屏幕宽高 */
        Constant.screenwith = wm.getDefaultDisplay().getWidth();
        Constant.screenheight = wm.getDefaultDisplay().getHeight();

        Constant.mainPadding = getResources().getDimension(R.dimen.main_left_right_margin);
        Constant.mainItemPadding = getResources().getDimension(R.dimen.main_item_padding_left_right);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private final String tabTitles[] = new String[]{"推荐", "开心", "美女", "电影", "音乐", "艺术", "广告"};
        private final String channel[] = {"Recommend", "Happy", "Beauty", "Movie", "Music", "Art", "Advertisement"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return IndexFragment.newInstance(channel[position]);
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//
//        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //dua.duaAwake();
        LogUtil.e("onResume is called");
        if(dua.getCurrentDuaUser().logon){
//            user_icon.setImageURI();  //更改头像图片
            user_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,DuaActivityProfile.class));
                }
            });
            userName.setText("已经登录");
            if(dua.duaUser.rules.contains("review_article")){
                reviewItem.setVisible(true);
            }
        }else {
            //user_icon.setImageAlpha(0);  //默认头像
            userName.setText(R.string.prompt_uer_name_default);
            user_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goLogin();
                }
            });
        }
    }

    public void goLogin(){
        startActivityForResult(new Intent(MainActivity.this, DuaActivityLogin.class),DUA_LOGIN_REQUEST_CODE);
    }



    @Override
    protected void onPause() {
        super.onPause();
        // dua.duaSleep();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ServiceConnection在使用的时候出现泄露了，原因是由于bind到service后，没有调用unbind进行释放。这个跟C/C++里的内存泄露应该是一类问题，资源使用完没有释放。
        //解决办法：程序退出前调用unbindService( )释放服务连接
        unbindService(ArticleServiceConnection);
    }

    class RomoteServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            com.lovearthstudio.articles.constant.Constant.binder = (ArticleService.ArticleBinder) service;
            handler.sendEmptyMessage(INIT_SUCCESS);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
