package com.iptv.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChannelAdapter adapter;
    ArrayList<Channel> channels = new ArrayList<>();

    String playlistUrl = "https://iptv-org.github.io/iptv/countries/in.m3u";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        EditText searchBar = findViewById(R.id.searchBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChannelAdapter(channels, this);
        recyclerView.setAdapter(adapter);

        // 🔍 Search logic
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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

                runOnUiThread(() -> {
                    adapter.fullList = new ArrayList<>(channels);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
