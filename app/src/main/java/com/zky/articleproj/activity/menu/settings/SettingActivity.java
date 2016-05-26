package com.zky.articleproj.activity.menu.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.lovearthstudio.duasdk.Dua;
import com.zky.articleproj.R;
import com.zky.articleproj.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {

    @ViewInject(R.id.other_toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.button_logout)
    private Button button_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!Dua.getInstance().getCurrentDuaUser().logon){
            button_logout.setEnabled(false);
            button_logout.setVisibility(View.GONE);
        }

    }

    @Event({R.id.button_logout})
    private void onClick(View view){
        Dua.getInstance().logout();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
