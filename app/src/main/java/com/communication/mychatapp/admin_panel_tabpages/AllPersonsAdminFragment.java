package com.communication.mychatapp.admin_panel_tabpages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.communication.mychatapp.R;
import com.communication.mychatapp.objectclasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllPersonsAdminFragment extends Fragment {

    ArrayAdapter<String> adapter;
    ListView listViewAllUsers;
    List<User> userList;
    List<String> stringUserList;
    FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.all_persons_admin_fragment,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewAllUsers=(ListView)view.findViewById(R.id.listViewAllPersonsAdmin);
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
                    stringUserList.add(user.userEmail);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
            }
        });
    }
}
