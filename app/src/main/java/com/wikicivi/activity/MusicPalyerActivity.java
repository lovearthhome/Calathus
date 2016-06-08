package com.wikicivi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wikicivi.R;
import com.wikicivi.manager.Player;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import co.mobiwise.playerview.MusicPlayerView;

@ContentView(R.layout.activity_music_palyer)
public class MusicPalyerActivity extends AppCompatActivity {

    @ViewInject(R.id.mpv)
    private MusicPlayerView mpv;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        player = new Player(mpv);

        String music_url = getIntent().getStringExtra("music_url");
        String image_url = getIntent().getStringExtra("image_url");
        if (music_url != null) {
            player.playUrl(music_url);

            //mpv.setCoverURL("https://upload.wikimedia.org/wikipedia/en/b/b3/MichaelsNumberOnes.JPG");

            mpv.setCoverURL(image_url);

            mpv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mpv.isRotating()) {
                        mpv.stop();
                        player.stop();
                    } else {
                        mpv.start();
                        player.play();
                    }
                }
            });
        }
    }
}
