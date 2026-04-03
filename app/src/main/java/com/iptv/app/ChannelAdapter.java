package com.iptv.app;

import android.content.*;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import java.util.*;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    ArrayList<Channel> list;
    Context context;

    public ChannelAdapter(ArrayList<Channel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(View v) {
            super(v);
            name = (TextView) v;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(context);
        tv.setPadding(20,20,20,20);
        tv.setTextSize(18);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Channel ch = list.get(position);
        holder.name.setText(ch.name);

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
