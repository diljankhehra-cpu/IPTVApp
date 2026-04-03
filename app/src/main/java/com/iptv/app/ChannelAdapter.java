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
    Context context;

    public ChannelAdapter(ArrayList<Channel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    // ✅ ViewHolder FIX
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(20, 20, 20, 20);
        tv.setTextSize(18);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Channel ch = list.get(position);
        holder.textView.setText(ch.name);

        // ✅ FIX: itemView properly use
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
}
