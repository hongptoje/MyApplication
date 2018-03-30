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
 * Created by hong on 2018-03-25.
 */

public class FindDirectionAdapter extends RecyclerView.Adapter<FindDirectionAdapter.FindDirectionViewHolder> {

    private ArrayList<FindDirectionItem> findDirectionList = new ArrayList<>();
    private Context context;

    public FindDirectionAdapter(ArrayList<FindDirectionItem> findDirectionList, Context context) {
        this.findDirectionList = findDirectionList;
        this.context = context;
    }

    @Override
    public FindDirectionAdapter.FindDirectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.direction_item, parent, false);
        return new FindDirectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FindDirectionAdapter.FindDirectionViewHolder holder, int position) {
        FindDirectionItem findDirectionItem = findDirectionList.get(position);
        holder.directionText.setText(findDirectionItem.getDirectionInfo());
        Glide.with(context).load(findDirectionItem.getDirectionImage()).into(holder.directionImage);
    }

    @Override
    public int getItemCount() {
        return findDirectionList.size();
    }

    public class FindDirectionViewHolder extends RecyclerView.ViewHolder {

        public TextView directionText;
        public ImageView directionImage;

        public FindDirectionViewHolder(View itemView) {
            super(itemView);
            directionText = (TextView)itemView.findViewById(R.id.directionText);
            directionImage = (ImageView)itemView.findViewById(R.id.directionImage);
        }
    }
}
