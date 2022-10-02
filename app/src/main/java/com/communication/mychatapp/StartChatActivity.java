package com.communication.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.communication.mychatapp.adapters.AdapterForRecyclerView;
import com.communication.mychatapp.objectclasses.Message;
import com.communication.mychatapp.objectclasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StartChatActivity extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference dbRef;
    User userChatPerson;
    User userNow;
    RecyclerView recyclerView;
    AdapterForRecyclerView adapter;
    Button buttonSendMessage;
    EditText editTextMessage;
    List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        Intent intent=getIntent();
        userChatPerson=new User();
        userChatPerson.setUserEmail(intent.getStringExtra("UserEmail"));
        userChatPerson.setUserName(intent.getStringExtra("UserName"));
        userChatPerson.setUserUid(intent.getStringExtra("UserUid"));

        ActionBar bar=getSupportActionBar();
        bar.setTitle(userChatPerson.getUserName());

        db= FirebaseDatabase.getInstance();

        recyclerView=(RecyclerView) findViewById(R.id.recyclerViewChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        buttonSendMessage=(Button)findViewById(R.id.buttonSendChatMessage);
        editTextMessage=(EditText)findViewById(R.id.editTextChatMessage);

        getCurrentUserInfoAndList();

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {

        } else {
            final Message message = new Message();
            message.setFromUid(userNow.getUserUid());
            message.setFromEmail(userNow.getUserEmail());
            message.setFromName(userNow.getUserName());
            message.setToUid(userChatPerson.getUserUid());
            message.setToEmail(userChatPerson.getUserEmail());
            message.setToName(userChatPerson.getUserName());
            message.setMessageText(messageText);
            message.setSentTime(Calendar.getInstance().getTimeInMillis());

            dbRef = db.getReference("mutual_messages");
            dbRef.push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), userChatPerson.getUserName() + " gönderildi", Toast.LENGTH_SHORT).show();
                        editTextMessage.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), userChatPerson.getUserName() + " gönderilemadi! Hata:" + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void getCurrentUserInfoAndList() {
        final FirebaseUser fBuser= FirebaseAuth.getInstance().getCurrentUser();
        db.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if((ds.getKey()).equals(fBuser.getUid())){
                        userNow=ds.getValue(User.class);
                    }
                }

                listChatMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listChatMessages() {
        messageList=new ArrayList<>();

        dbRef=db.getReference("mutual_messages");
        if(dbRef!=null){
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    messageList.clear();
                    for(DataSnapshot ds1:dataSnapshot.getChildren()) {
                        Message message = ds1.getValue(Message.class);
                        if((message.getFromUid()).equals(userNow.getUserUid())&&(message.getToUid()).equals(userChatPerson.getUserUid())){
                            message.setAlignment(1);
                            messageList.add(message);
                        }

                        if((message.getFromUid()).equals(userChatPerson.getUserUid())&& (message.getToUid()).equals(userNow.getUserUid())){
                            message.setAlignment(0);
                            messageList.add(message);
                        }
                    }

                    Collections.sort(messageList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        adapter=new AdapterForRecyclerView(getApplicationContext(),messageList);
        recyclerView.setAdapter(adapter);
        recyclerView.findViewHolderForAdapterPosition(messageList.size()-1);
    }
}
