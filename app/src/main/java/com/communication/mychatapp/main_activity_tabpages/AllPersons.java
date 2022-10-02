package com.communication.mychatapp.main_activity_tabpages;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class AllPersons extends Fragment {

    ArrayAdapter<String> adapter;
    ListView listViewAllUsers;
    List<User> userList;
    List<String> stringUserList;
    FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.all_persons_fragment,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewAllUsers=(ListView)view.findViewById(R.id.listViewAllPersons);
        userList=new ArrayList<>();
        stringUserList=new ArrayList<>();
        db=FirebaseDatabase.getInstance();

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stringUserList);
        listViewAllUsers.setAdapter(adapter);


        db.getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                stringUserList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user=ds.getValue(User.class);
                    userList.add(user);
                    stringUserList.add(user.getUserEmail());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
            }
        });

        listViewAllUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showListViewItemClickAlertDialog(userList.get(i));
            }
        });
    }

    private void showListViewItemClickAlertDialog(final User _user){
        final FirebaseUser userNow= FirebaseAuth.getInstance().getCurrentUser();
        final User[] user = {new User()};
        db.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals(userNow.getUid())){
                        user[0] =ds.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.send_message_request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference dbRef=db.getReference("/users_mutual_messages_requests//"+_user.getUserUid()+"/"+userNow.getUid());
                dbRef.setValue(user[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("New Message",_user.getUserEmail()+" :gönderildi");
                        }else{
                            Log.e("New Message",""+task.getException().getLocalizedMessage());
                        }
                    }
                });

                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(R.string.send_add_person_request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DatabaseReference dbRef=db.getReference("/users_friendship_requests//"+_user.getUserUid()+"/"+ userNow.getUid());
                dbRef.setValue(user[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("New Person Requeest",user[0].getUserName()+" :gönderildi");
                        }else{
                            Log.e("New Person Requeest",""+task.getException().getLocalizedMessage());
                        }
                    }
                });

                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
