package com.example.hong.myapplication;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

/**
 * Created by hong on 2018-02-27.
 */

//fcm 메시지를 받아서 처리하는 코드, 메시지를 수신하면 제목을 팝업 메시지로 띄우고 클릭하면 내용을 볼수 있는 곳으로 이동하도록 구현
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //메시지 받았을 때 동작하는 메소드 fcm에 전달된 데이터를 가져옴
        String push_message = remoteMessage.getData().get("message");
        String push_title = remoteMessage.getData().get("title");
        String push_room_number = remoteMessage.getData().get("pushRoomNumber");
        String push_friend_id = remoteMessage.getData().get("pushFriendId");
        Log.d("serviceLog","push_friend_id: "+push_friend_id);
        SharedPreferences room_info = getApplicationContext().getSharedPreferences("friend_info", Context.MODE_PRIVATE);
        String chech_push = room_info.getString("room_number","");
        Log.d("serviceLog","push_room_number: "+push_room_number+"check_push_room: "+chech_push);
        if (!push_room_number.equals(chech_push)) {
            SharedPreferences.Editor editor = room_info.edit();
            editor.putString("room_number", push_room_number);
            editor.putString("id", push_friend_id);
            editor.commit();
            sendNotification(push_message, push_title);
        }
    }

    //받은 메시지를 핸드폰 상단알림 화면에 띄움
    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, chatting.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //보내는 사람
                .setContentTitle(title)
                //채팅 내용
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }


}
