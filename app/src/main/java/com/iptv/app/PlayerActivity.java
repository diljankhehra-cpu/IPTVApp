package com.iptv.app;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {

    VideoView videoView;
    ProgressBar loading;
    TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 📦 Layout
        videoView = new VideoView(this);
        loading = new ProgressBar(this);
        statusText = new TextView(this);

        loading.setIndeterminate(true);

        statusText.setTextColor(0xFFFFFFFF);
        statusText.setGravity(Gravity.CENTER);
        statusText.setTextSize(16);

        FrameLayout layout = new FrameLayout(this);

        layout.addView(videoView);

        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        progressParams.gravity = Gravity.CENTER;

        layout.addView(loading, progressParams);

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = Gravity.CENTER;

        layout.addView(statusText, textParams);

        setContentView(layout);

        String url = getIntent().getStringExtra("url");

        playVideo(url);
    }

    private void playVideo(String url) {

        loading.setVisibility(ProgressBar.VISIBLE);
        statusText.setText("Buffering...");

        videoView.setVideoURI(Uri.parse(url));

        videoView.setOnPreparedListener(mp -> {
            loading.setVisibility(ProgressBar.GONE);
            statusText.setText("");
            videoView.start();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            loading.setVisibility(ProgressBar.GONE);

            String errorMsg = "Playback Error";

            if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                errorMsg = "Server Error (500)";
            } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                errorMsg = "Stream Error (404 / 503)";
            }

            statusText.setText(errorMsg);
            return true;
        });

        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                loading.setVisibility(ProgressBar.VISIBLE);
                statusText.setText("Buffering...");
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                loading.setVisibility(ProgressBar.GONE);
                statusText.setText("");
            }
            return false;
        });
    }
}
