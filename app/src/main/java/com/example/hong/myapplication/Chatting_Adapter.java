package com.example.hong.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by hong on 2018-02-04.
 */

public class Chatting_Adapter extends RecyclerView.Adapter<Chatting_Adapter.ChattingViewHolder> {

    private ArrayList<chatting_item> chatting_list = new ArrayList<>();
    private Context mcontext;

    public Chatting_Adapter(Context context, ArrayList<chatting_item> chatting_list) {
        this.mcontext = context;
        this.chatting_list = chatting_list;
    }

    @Override
    public ChattingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_item, parent, false);
        return new ChattingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChattingViewHolder holder, int position) {
        chatting_item chatting_item = chatting_list.get(position);
        //채팅을 보낸 사람과 메시지를 받는 사람이 같은경우
        if (chatting_list.get(position).getPosition_type() == 1) {
            holder.chatting_image.setVisibility(View.INVISIBLE);
            holder.chatting_message.setVisibility(View.INVISIBLE);
            holder.chatting_name.setVisibility(View.INVISIBLE);
            holder.chatting_message_right.setVisibility(View.VISIBLE);
            holder.chatting_message.setText(chatting_item.getChatting_message());
            holder.chatting_message_right.setText(chatting_item.getChatting_message());
            holder.chatting_name.setText(chatting_item.getChatting_name());
            Glide.with(mcontext).load(chatting_item.getChatting_image()).apply(bitmapTransform(new CircleCrop())).into(holder.chatting_image);
            if (chatting_list.get(position).getChatting_message().equals("#지금만나")) {
                holder.chatting_message_right.setTextColor(Color.parseColor("#FF002AFF"));
                holder.chatting_message_right.setPaintFlags(holder.chatting_message_right.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.chatting_message_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mcontext, MapsActivity.class);
                        mcontext.startActivity(intent);
                    }
                });
            }
        } else if (chatting_list.get(position).getPosition_type() == 0) {
            if (chatting_list.get(position).getChatting_message().equals("#지금만나")) {
                holder.chatting_message.setTextColor(Color.parseColor("#FF002AFF"));
                holder.chatting_message.setPaintFlags(holder.chatting_message.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.chatting_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mcontext, MapsActivity.class);
                        mcontext.startActivity(intent);
                    }
                });
            }
            if (chatting_list.get(position).getMessage_again() == 1) {
                holder.chatting_image.setVisibility(View.INVISIBLE);
                holder.chatting_message.setVisibility(View.VISIBLE);
                holder.chatting_name.setVisibility(View.INVISIBLE);
                holder.chatting_message_right.setVisibility(View.INVISIBLE);
                holder.chatting_message.setText(chatting_item.getChatting_message());
                holder.chatting_message_right.setText(chatting_item.getChatting_message());
                holder.chatting_name.setText(chatting_item.getChatting_name());
                Glide.with(mcontext).load(chatting_item.getChatting_image()).apply(bitmapTransform(new CircleCrop())).into(holder.chatting_image);
            } else {
                holder.chatting_image.setVisibility(View.VISIBLE);
                holder.chatting_message.setVisibility(View.VISIBLE);
                holder.chatting_name.setVisibility(View.VISIBLE);
                holder.chatting_message_right.setVisibility(View.INVISIBLE);
                holder.chatting_message.setText(chatting_item.getChatting_message());
                holder.chatting_message_right.setText(chatting_item.getChatting_message());
                holder.chatting_name.setText(chatting_item.getChatting_name());
                Glide.with(mcontext).load(chatting_item.getChatting_image()).apply(bitmapTransform(new CircleCrop())).into(holder.chatting_image);
            }
        }


    }

    @Override
    public int getItemCount() {
        return chatting_list.size();
    }

    public static class ChattingViewHolder extends RecyclerView.ViewHolder {
        public TextView chatting_message;
        public TextView chatting_name;
        public ImageView chatting_image;
        public TextView chatting_message_right;

        public ChattingViewHolder(View itemView) {
            super(itemView);
            chatting_message = (TextView) itemView.findViewById(R.id.chatting_message);
            chatting_name = (TextView) itemView.findViewById(R.id.chatting_name);
            chatting_image = (ImageView) itemView.findViewById(R.id.chatting_image);
            chatting_message_right = (TextView) itemView.findViewById(R.id.chatting_message_right);

//            if(get_id.equals(receive_id)){
//                chatting_name.setVisibility(View.INVISIBLE);
//                chatting_image.setVisibility(View.INVISIBLE);
//            }
        }
    }
}
