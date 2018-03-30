package com.example.hong.myapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by hong on 2018-01-30.
 */

public class adapter extends RecyclerView.Adapter<adapter.ViewHolder>{

    private ArrayList<item> friend_list = new ArrayList<>();
    private Context context;

    public adapter(Context context, ArrayList<item> friend_list){
        this.context = context;
        this.friend_list = friend_list;

    }

    @Override
    public adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(adapter.ViewHolder holder, int position) {
        item friend_item = friend_list.get(position);
        holder.friend_name.setText(friend_item.getFriend_name());
        holder.friend_state_message.setText(friend_item.getFriend_state_message());
        Glide.with(context).load(friend_item.getFriend_image()).apply(bitmapTransform(new CircleCrop())).into(holder.friend_image);
        holder.friend_id.setText(friend_item.getFriend_id());
    }

    @Override
    public int getItemCount() {
        return friend_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView friend_name;
        public TextView friend_state_message;
        public ImageView friend_image;
        public TextView friend_id;
        public ViewHolder(View itemView) {
            super(itemView);
            friend_name = (TextView)itemView.findViewById(R.id.friend_name);
            friend_state_message = (TextView)itemView.findViewById(R.id.friend_state_message);
            friend_image = (ImageView)itemView.findViewById(R.id.friend_image);
            friend_id = (TextView)itemView.findViewById(R.id.friend_id);
            //그냥 클릭했을 땐 친구 상세정보
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String name = friend_list.get(position).getFriend_name();
            String state_message = friend_list.get(position).getFriend_state_message();
            String image = friend_list.get(position).getFriend_image();
            String id = friend_list.get(position).getFriend_id();


            SharedPreferences friend_info = context.getSharedPreferences("friend_info", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = friend_info.edit();
            editor.putString("name", name);
            editor.putString("state_message", state_message);
            editor.putString("image", String.valueOf(image));
            editor.putString("id", id);
            editor.commit();
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("name", name);
//                jsonObject.put("state_message", state_message);
//                jsonObject.put("image", image);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            Intent intent = new Intent(context, friend_detail.class);
            context.startActivity(intent);
        }

    }
}
