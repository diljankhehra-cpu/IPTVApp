package com.iptv.app;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    MediaPlayer mediaPlayer;
    ProgressBar loading;
    TextView statusText;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surfaceView = new SurfaceView(this);
        loading = new ProgressBar(this);
        statusText = new TextView(this);

        statusText.setTextColor(0xFFFFFFFF);
        statusText.setGravity(Gravity.CENTER);
        statusText.setTextSize(16);

        FrameLayout layout = new FrameLayout(this);
        layout.addView(surfaceView);

        FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        p1.gravity = Gravity.CENTER;
        layout.addView(loading, p1);

        FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        p2.gravity = Gravity.CENTER;
        layout.addView(statusText, p2);

        setContentView(layout);

        // ✅ URL receive
        url = getIntent().getStringExtra("url");

        if (url == null || url.isEmpty()) {
            statusText.setText("Invalid URL");
            return;
        }

        SurfaceHolder holder = surfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                playStream(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mediaPlayer != null) mediaPlayer.release();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });
    }

    private void playStream(SurfaceHolder holder) {
        try {
            loading.setVisibility(ProgressBar.VISIBLE);
            statusText.setText("Buffering...");

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(url));
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnPreparedListener(mp -> {
                loading.setVisibility(ProgressBar.GONE);
                statusText.setText("");
                mediaPlayer.start();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                loading.setVisibility(ProgressBar.GONE);
                statusText.setText("Stream Error");
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            statusText.setText("Invalid Stream");
        }
    }
}
