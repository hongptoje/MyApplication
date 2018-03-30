package com.example.hong.myapplication;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class Main extends AppCompatActivity {
ImageButton friend_button, talk_button, setting_button;
ViewPager pager;
pagerAdapter pagerAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        pager = (ViewPager)findViewById(R.id.pager);
        friend_button = (ImageButton)findViewById(R.id.friend_button);
        talk_button = (ImageButton)findViewById(R.id.talk_button);
        setting_button = (ImageButton)findViewById(R.id.setting_button);

        pagerAdapter = new pagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0);
        View.OnClickListener movePageListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int)v.getTag();
                pager.setCurrentItem(tag);
            }
        };
        friend_button.setOnClickListener(movePageListener);
        friend_button.setTag(0);
        talk_button.setOnClickListener(movePageListener);
        talk_button.setTag(1);
        setting_button.setOnClickListener(movePageListener);
        setting_button.setTag(2);

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_Token",""+token);
    }

    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(FragmentManager fm )
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return new friend();
                case 1:
                    return new talk();
                case 2:
                    return new setting();
                default:
                    return null;
            }
        }
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;

        }

        @Override
        public int getCount() {
            // total page count
            return 3;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFirebaseDatabase();
//        pagerAdapter.notifyDataSetChanged();
    }

    private void initFirebaseDatabase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("message");
        //firebase realtime database가 업데이트 되면 pagerAdapter.notifyDataSetChanged를 통해 방정보를 갱신한다.
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }
}
