package com.example.hong.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by hong on 2018-02-13.
 */

public class Chatting_Room_Adapter extends RecyclerView.Adapter<Chatting_Room_Adapter.ChattingRoomViewHolder> {

    private ArrayList<Chatting_Room_Item> chatting_room_list = new ArrayList<>();
    private Context context;

    public Chatting_Room_Adapter(Context context, ArrayList<Chatting_Room_Item> chatting_room_list) {
        this.chatting_room_list = chatting_room_list;
        this.context = context;
    }

    @Override
    public Chatting_Room_Adapter.ChattingRoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_room_item, parent, false);
        return new ChattingRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Chatting_Room_Adapter.ChattingRoomViewHolder holder, int position) {
        Chatting_Room_Item chatting_room_item = chatting_room_list.get(position);
        holder.room_name.setText(chatting_room_item.getRoom_name());
        holder.message_preview.setText(chatting_room_item.getMessage_preview());
        holder.room_number.setText(chatting_room_item.getRoom_number());
        holder.room_friend_id.setText(chatting_room_item.getRoom_friend_id());
        holder.receive_time.setText(chatting_room_item.getReceive_time());
        Glide.with(context).load(chatting_room_item.getRoom_image()).apply(bitmapTransform(new CircleCrop())).into(holder.room_image);
    }

    @Override
    public int getItemCount() {
        return chatting_room_list.size();
    }

    public class ChattingRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView room_image;
        public TextView room_name;
        public TextView message_preview;
        public TextView room_number;
        public TextView room_friend_id;
        public TextView receive_time;

        public ChattingRoomViewHolder(View itemView) {
            super(itemView);
            room_image = (ImageView)itemView.findViewById(R.id.room_image);
            room_name = (TextView)itemView.findViewById(R.id.room_name);
            room_friend_id = (TextView)itemView.findViewById(R.id.room_friend_id);
            room_number = (TextView)itemView.findViewById(R.id.room_number);
            message_preview = (TextView)itemView.findViewById(R.id.message_preview);
            receive_time = (TextView)itemView.findViewById(R.id.receive_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String name = chatting_room_list.get(position).getRoom_name();
            String image = chatting_room_list.get(position).getRoom_image();
            String id = chatting_room_list.get(position).getRoom_friend_id();
            String state_message = chatting_room_list.get(position).getMessage_preview();
            String room_number = chatting_room_list.get(position).getRoom_number();

            SharedPreferences room_info = context.getSharedPreferences("friend_info", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = room_info.edit();
            editor.putString("name", name);
            editor.putString("image", String.valueOf(image));
            editor.putString("id", id);
            editor.putString("state_message", state_message);
            editor.putString("room_number", room_number);
            editor.commit();
            Intent intent = new Intent(context, chatting.class);
            context.startActivity(intent);
        }
    }
}
