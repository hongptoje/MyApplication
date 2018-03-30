package com.example.hong.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by hong on 2018-03-29.
 */

public class TransitDirectionAdapter extends RecyclerView.Adapter<TransitDirectionAdapter.TransitDirectionViewHolder>{

    private ArrayList<TransitDirectionItem> transitDirectionList = new ArrayList<>();
    private Context context;

    public TransitDirectionAdapter(ArrayList<TransitDirectionItem> transitDirectionList, Context context) {
        this.transitDirectionList = transitDirectionList;
        this.context = context;
    }

    @Override
    public TransitDirectionAdapter.TransitDirectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transit_direction_item, parent, false);
        return new TransitDirectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransitDirectionAdapter.TransitDirectionViewHolder holder, int position) {
        TransitDirectionItem transitDirectionItem = transitDirectionList.get(position);
        holder.transitDirectionTitleInfo.setText(transitDirectionItem.getTransitDirectionTitleInfo());
        holder.transitDirectionSubtitleInfo.setText(transitDirectionItem.getTransitDirectionSubtitleInfo());
        holder.transitDuration.setText(transitDirectionItem.getTransitDuration());
        Glide.with(context).load(transitDirectionItem.getTransitDirectionImage()).into(holder.transitDirectionImage);
    }

    @Override
    public int getItemCount() {
        return transitDirectionList.size();
    }

    public class TransitDirectionViewHolder extends RecyclerView.ViewHolder {

        public TextView transitDirectionTitleInfo;
        public TextView transitDirectionSubtitleInfo;
        public ImageView transitDirectionImage;
        public TextView transitDuration;

        public TransitDirectionViewHolder(View itemView) {
            super(itemView);
            transitDirectionTitleInfo = (TextView)itemView.findViewById(R.id.transitDirectionTitleInfo);
            transitDirectionSubtitleInfo = (TextView)itemView.findViewById(R.id.transitDirectionSubtitleInfo);
            transitDirectionImage = (ImageView)itemView.findViewById(R.id.transitdirectionImage);
            transitDuration = (TextView)itemView.findViewById(R.id.transitDuration);
        }
    }
}
