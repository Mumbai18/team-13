package com.ksapps.shelfshare;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ChatRoomActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String username;
    String msgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        username = mUser.getDisplayName();

        Intent i = getIntent();
        msgReceiver = i.getStringExtra("messageReceiver");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(username).child("message").child(msgReceiver).push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                username, msgReceiver)
                        );
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(msgReceiver).child("message").child(username).push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                username, msgReceiver)
                        );
                input.setText("");
            }
        });

        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(username).child("message").child(msgReceiver)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                Log.v("USERNAME",msgReceiver);
                if(model.getMessageReceiver().equals(msgReceiver)||model.getMessageSender().equals(msgReceiver)) {
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageSender = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                // Set their text
                    if (!model.getMessageSender().equals(username)) {
                        messageText.setText(model.getMessageText());
                        messageSender.setText(model.getMessageSender());
                    } else {
                        messageText.setText(model.getMessageText());
                        messageSender.setText("You");
                    }
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));
                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ChatRoomActivity.this, ChatListActivity.class);
        startActivity(i);
        finish();
    }
}
