package com.communication.mychatapp.main_activity_tabpages;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.List;

public class MyPersons extends Fragment {

    ListView listViewFriendshipRequests;
    ListView listViewMyFriends;
    ArrayAdapter<String> adapterFriendshipRequests;
    ArrayAdapter<String> adapterMyFriends;
    FirebaseDatabase db;
    DatabaseReference dbRef;
    User userNow;
    List<User> friendshipRequestsList;
    List<String> stringFriendshipRequestsList;
    List<User> friendsList;
    List<String> stringFriendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_persons_fragment,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewMyFriends=(ListView)view.findViewById(R.id.listView_my_friends);
        listViewFriendshipRequests=(ListView)view.findViewById(R.id.listView_friendship_requests);

        db=FirebaseDatabase.getInstance();

        getCurrentUserInfoAndList();

        listViewFriendshipRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                showListViewFriendshipItemClickAlertDialog(pos);
            }
        });

        listViewMyFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int pos, long l) {
                showlistViewMyFriendsAlerDialog(pos);
            }
        });
    }

    private void showlistViewMyFriendsAlerDialog(final int pos) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.send_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getActivity(), StartChatActivity.class);

                intent.putExtra("UserEmail",friendsList.get(pos).getUserEmail());
                intent.putExtra("UserName",friendsList.get(pos).getUserName());
                intent.putExtra("UserUid",friendsList.get(pos).getUserUid());

                startActivity(intent);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void getCurrentUserInfoAndList() {
        final FirebaseUser fBuser= FirebaseAuth.getInstance().getCurrentUser();
        db.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals(fBuser.getUid())){
                        userNow=ds.getValue(User.class);
                    }
                }

                listFriendshipRequests();
                listMyFriends();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showListViewFriendshipItemClickAlertDialog(final int pos) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbRef=db.getReference("/users_friends/"+userNow.getUserUid()+"/"+friendshipRequestsList.get(pos).getUserUid());
                dbRef.setValue(friendshipRequestsList.get(pos)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("New Friend",friendshipRequestsList.get(pos).getUserName()+" :eklendi");
                            dbRef=db.getReference("/users_friends/"+friendshipRequestsList.get(pos).getUserUid()+"/"+userNow.getUserUid());
                            dbRef.setValue(userNow).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.e("New Friend",userNow.getUserName()+" :eklendi");
                                        dbRef=db.getReference("/users_friendship_requests/"+userNow.getUserUid()+"/"+friendshipRequestsList.get(pos).getUserUid());
                                        dbRef.removeValue();

                                        dbRef=db.getReference("/users_friendship_requests/"+friendshipRequestsList.get(pos).getUserUid()+"/"+userNow.getUserUid());
                                        if(dbRef!=null){
                                            dbRef.removeValue();
                                        }

                                    }else{
                                        Log.e("New Friend",""+task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                        }else{
                            Log.e("New Friend",""+task.getException().getLocalizedMessage());
                        }
                    }
                });
            }
        });

        builder.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbRef=db.getReference("/users_friendship_requests/"+userNow.getUserUid()+"/"+friendshipRequestsList.get(pos).getUserUid());
                dbRef.removeValue();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void listMyFriends() {
        friendsList=new ArrayList<>();
        stringFriendsList=new ArrayList<>();

        dbRef=db.getReference("/users_friends/"+userNow.getUserUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsList.clear();
                stringFriendsList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user=ds.getValue(User.class);
                    friendsList.add(user);
                    stringFriendsList.add(user.getUserEmail());
                }
                adapterMyFriends.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapterMyFriends=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,stringFriendsList);
        listViewMyFriends.setAdapter(adapterMyFriends);
    }

    private void listFriendshipRequests() {
        friendshipRequestsList=new ArrayList<>();
        stringFriendshipRequestsList=new ArrayList<>();

        dbRef=db.getReference("/users_friendship_requests/"+userNow.getUserUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendshipRequestsList.clear();
                stringFriendshipRequestsList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user=ds.getValue(User.class);
                    friendshipRequestsList.add(user);
                    stringFriendshipRequestsList.add(user.getUserEmail());
                }
                adapterFriendshipRequests.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapterFriendshipRequests=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,stringFriendshipRequestsList);
        listViewFriendshipRequests.setAdapter(adapterFriendshipRequests);
    }
}
