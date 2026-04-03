package com.iptv.app;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class PlayerActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    MediaPlayer mediaPlayer;
    ProgressBar loading;
    TextView statusText;

    // 🔥 PLAYLIST URL
    String playlistUrl = "https://iptv-org.github.io/iptv/index.m3u";

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

        SurfaceHolder holder = surfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                loadPlaylistAndPlay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
            }
        });
    }

    // 🔥 Playlist load karke first stream play karega
    private void loadPlaylistAndPlay(SurfaceHolder holder) {
        new Thread(() -> {
            try {
                runOnUiThread(() -> {
                    loading.setVisibility(ProgressBar.VISIBLE);
                    statusText.setText("Loading playlist...");
                });

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new URL(playlistUrl).openStream())
                );

                String line;
                String streamUrl = null;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("http")) {
                        streamUrl = line;
                        break;
                    }
                }

                br.close();

                if (streamUrl == null) {
                    runOnUiThread(() -> statusText.setText("No stream found"));
                    return;
                }

                String finalStreamUrl = streamUrl;

                runOnUiThread(() -> playStream(holder, finalStreamUrl));

            } catch (Exception e) {
                runOnUiThread(() -> statusText.setText("Playlist Error"));
            }
        }).start();
    }

    private void playStream(SurfaceHolder holder, String url) {
        try {
            statusText.setText("Buffering...");

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnPreparedListener(mp -> {
                loading.setVisibility(ProgressBar.GONE);
                statusText.setText("");
                mediaPlayer.start();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                loading.setVisibility(ProgressBar.GONE);
                statusText.setText("Stream Error (404 / 503)");
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            statusText.setText("Invalid Stream");
        }
    }
}
