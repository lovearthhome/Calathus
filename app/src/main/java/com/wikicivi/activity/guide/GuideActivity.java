package com.wikicivi.activity.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;
import com.wikicivi.R;
import com.wikicivi.activity.BaseActivity;
import com.wikicivi.activity.MainActivity;
import com.wikicivi.activity.guide.guidepages.GuideOneFragment;
import com.wikicivi.activity.guide.guidepages.GuideThreeFragment;
import com.wikicivi.activity.guide.guidepages.GuideTwoFragment;
import com.wikicivi.fragment.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_guide)
public class GuideActivity extends BaseActivity {

    @ViewInject(R.id.guide_pagers)
    private ViewPager guide_pagers;
    @ViewInject(R.id.my_indicator)
    private CirclePageIndicator indicator;

    @ViewInject(R.id.btn_start)
    private Button btn_start;

    private List<BaseFragment> pagers;

    @Event({R.id.btn_start})
    private void click(View view) {
        startActivity(new Intent(this, MainActivity.class));
        getSharedPreferences("config", MODE_PRIVATE).edit().putBoolean("isfirststart", false).commit();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        guide_pagers.setAdapter(new GuidePagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(guide_pagers);
        guide_pagers.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == pagers.size() - 1) {
                    btn_start.setVisibility(View.VISIBLE);
                } else {
                    btn_start.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class GuidePagerAdapter extends FragmentStatePagerAdapter {

        public GuidePagerAdapter(FragmentManager fm) {
            super(fm);

            pagers = new ArrayList<>();
            pagers.add(new GuideOneFragment());
            pagers.add(new GuideTwoFragment());
            pagers.add(new GuideThreeFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return pagers.get(position);
        }

        @Override
        public int getCount() {
            return pagers.size();
        }
    }
}
