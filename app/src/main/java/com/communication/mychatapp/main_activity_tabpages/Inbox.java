package com.communication.mychatapp.main_activity_tabpages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.communication.mychatapp.R;
import com.communication.mychatapp.StartChatActivity;
import com.communication.mychatapp.objectclasses.Message;
import com.communication.mychatapp.objectclasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Inbox extends Fragment {

    ListView listViewMessages;
    ArrayAdapter<String> adapter;
    FirebaseDatabase db;
    DatabaseReference dbRef;
    User userNow;
    List<User> listSentAndInBoxCombined;
    List<String> listSentAndInBoxCombinedString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inbox_fragment,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewMessages=(ListView)view.findViewById(R.id.listViewMessages);

        db=FirebaseDatabase.getInstance();

        getCurrentUserInfoAndListMessages();

        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                Intent intent=new Intent(getActivity(), StartChatActivity.class);

                intent.putExtra("UserEmail",listSentAndInBoxCombined.get(pos).getUserEmail());
                intent.putExtra("UserName",listSentAndInBoxCombined.get(pos).getUserName());
                intent.putExtra("UserUid",listSentAndInBoxCombined.get(pos).getUserUid());

                Log.e("MessageUsers",listSentAndInBoxCombined.get(pos).getUserName()+"\n"+listSentAndInBoxCombined.get(pos).getUserEmail()+"\n"+listSentAndInBoxCombined.get(pos).getUserUid());

                startActivity(intent);
            }
        });

    }

    private void listMyMessages() {
        listSentAndInBoxCombined=new ArrayList<>();
        listSentAndInBoxCombinedString=new ArrayList<>();

        final Set<User> setSentAndInBoxCombined=new TreeSet<User>();
        dbRef=db.getReference("mutual_messages");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listSentAndInBoxCombined.clear();
                listSentAndInBoxCombinedString.clear();
                setSentAndInBoxCombined.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Message message=ds.getValue(Message.class);
                    if((message.getFromUid()).equals(userNow.getUserUid())){
                        User userTo=new User();
                        userTo.setUserUid(message.getToUid());
                        userTo.setUserEmail(message.getToEmail());
                        userTo.setUserName(message.getToName());
                        setSentAndInBoxCombined.add(userTo);
                    }
                    if((message.getToUid()).equals(userNow.getUserUid())){
                       User userFrom=new User();
                       userFrom.setUserName(message.getFromName());
                       userFrom.setUserEmail(message.getFromEmail());
                       userFrom.setUserUid(message.getFromUid());
                        setSentAndInBoxCombined.add(userFrom);
                    }
                }

                for(User user_:setSentAndInBoxCombined){
                    listSentAndInBoxCombined.add(user_);
                    listSentAndInBoxCombinedString.add(user_.getUserName()+"\n"+user_.getUserEmail());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listSentAndInBoxCombinedString);
        listViewMessages.setAdapter(adapter);
    }

    private void getCurrentUserInfoAndListMessages() {
        final FirebaseUser fBuser = FirebaseAuth.getInstance().getCurrentUser();
        db.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if ((ds.getKey()).equals(fBuser.getUid())) {
                        userNow = ds.getValue(User.class);
                    }
                }
                listMyMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private User getUserInfos(final String uid){
        final User[] userFind = {new User()};
        db.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if ((ds.getKey()).equals(uid)) {
                        userFind[0] = ds.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return userFind[0];
    }
}
