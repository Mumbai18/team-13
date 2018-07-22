package com.ksapps.shelfshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    private ListView lvBooksList;
    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ListView lvChatList;
    String mUser1;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUser1 = mUser.getDisplayName();

        mDataRef = FirebaseDatabase.getInstance().getReference();

        lvChatList = (ListView)findViewById(R.id.lvChatList);

        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object o = dataSnapshot.child(mUser1).getValue();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String val = ds.getValue().toString();

                    String v2 = ds.getValue(UsersInfo.class).getUser_name();
                    if(v2!=null && !val.contains("ad_title")&&!ds.getValue().equals(o))

                        arrayList.add(v2);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.v("CHECK","helloC");
        arrayAdapter = new ArrayAdapter<>(ChatListActivity.this, android.R.layout.simple_list_item_1, arrayList);
        lvChatList.setAdapter(arrayAdapter);

        lvChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChatListActivity.this, ChatRoomActivity.class);
                intent.putExtra("messageReceiver",arrayList.get(i));
                Log.v("USERNAME",arrayList.get(i));
                startActivity(intent);
                finish();
            }
        });
    }
}
