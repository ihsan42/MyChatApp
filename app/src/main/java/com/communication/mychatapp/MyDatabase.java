package com.communication.mychatapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.communication.mychatapp.objectclasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDatabase {

    FirebaseDatabase db;
    DatabaseReference dbRef;
    List<User> userList;

    public MyDatabase(){
        db=FirebaseDatabase.getInstance();
        dbRef=db.getReference();
        userList=new ArrayList<>();
    }

    public List<User> getUsers(){
         db.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user=ds.getValue(User.class);
                    userList.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
            }
        });
        return  userList;
    }

    public void addUser(final FirebaseUser user,String userName,Boolean isAdmin){
        User _user=new User();
        _user.setUserName(userName);
        _user.setAdmin(isAdmin);
        _user.setUserEmail(user.getEmail());
        _user.setUserCreationTimeStamp(user.getMetadata().getCreationTimestamp());
        _user.setUserLastSignInTimeStamp(Calendar.getInstance().getTimeInMillis());

        db.getReference().child("users").child(user.getUid()).setValue(_user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.e("User","eklendi");
                }else{
                    Log.e("User",""+task.getException().getLocalizedMessage());
                }
            }
        });
    }

    public void addUserAdmin(final Context context, final User _user){
        //Log.e("EEEEEEUid: ",_user.getUserUid());
        db.getReference().child("users").child(_user.getUserUid()).setValue(_user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,_user.getUserName()+" eklendi",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,_user.getUserName()+" eklenemedi! "+task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateUser(FirebaseUser user,String userName,boolean isAdmin){
        User _user=new User();
        _user.setAdmin(isAdmin);
        _user.setUserName(userName);
        _user.setUserEmail(user.getEmail());
        _user.setUserUid(user.getUid());
        _user.setUserCreationTimeStamp(user.getMetadata().getCreationTimestamp());
        _user.setUserLastSignInTimeStamp(Calendar.getInstance().getTimeInMillis());

        Map<String,Object> postValues=_user.toMap();

        Map<String,Object> childValues=new HashMap<>();
        childValues.put("/users/"+_user.getUserUid(),postValues);
        dbRef.updateChildren(childValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.e("User","güncellendi");
                }else{
                    Log.e("User",""+task.getException().getLocalizedMessage());
                }
            }
        });
    }

    public void deleteUser(User _user){
       dbRef=db.getReference("/sign_up_requests/"+_user.getUserUid());
       dbRef.removeValue();
    }
}
