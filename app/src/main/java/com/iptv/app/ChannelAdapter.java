package com.iptv.app;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    ArrayList<Channel> list;
    ArrayList<Channel> fullList;
    Context context;

    public ChannelAdapter(ArrayList<Channel> list, Context context) {
        this.list = list;
        this.fullList = new ArrayList<>(list);
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(context);
        tv.setPadding(20, 20, 20, 20);
        tv.setTextSize(18);
        tv.setTextColor(0xFFFFFFFF);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Channel ch = list.get(position);
        holder.textView.setText(ch.name);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, PlayerActivity.class);
            i.putExtra("url", ch.url);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 🔍 FILTER METHOD
    public void filter(String text) {
        list.clear();

        if (text.isEmpty()) {
            list.addAll(fullList);
        } else {
            text = text.toLowerCase();
            for (Channel ch : fullList) {
                if (ch.name.toLowerCase().contains(text)) {
                    list.add(ch);
                }
            }
        }
        notifyDataSetChanged();
    }
}
