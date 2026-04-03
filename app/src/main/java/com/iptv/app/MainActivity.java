package com.iptv.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChannelAdapter adapter;
    ArrayList<Channel> channels = new ArrayList<>();

    String playlistUrl = "https://iptv-org.github.io/iptv/countries/in.m3u";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = new RecyclerView(this);
        setContentView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChannelAdapter(channels, this);
        recyclerView.setAdapter(adapter);

        loadM3U();
    }

    void loadM3U() {
        new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(new URL(playlistUrl).openStream())
                );

                String line;
                String name = "";

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("#EXTINF")) {
                        name = line.substring(line.indexOf(",") + 1);
                    } else if (line.startsWith("http")) {
                        channels.add(new Channel(name, line));
                    }
                }

                br.close();

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
