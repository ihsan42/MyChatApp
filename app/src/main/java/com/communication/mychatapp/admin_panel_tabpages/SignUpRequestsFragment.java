package com.communication.mychatapp.admin_panel_tabpages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.communication.mychatapp.MyDatabase;
import com.communication.mychatapp.R;
import com.communication.mychatapp.SendEmail;
import com.communication.mychatapp.objectclasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class SignUpRequestsFragment extends Fragment {

    ArrayAdapter<String> adapter;
    ListView listViewAllSignUpRequests;
    List<User> userList;
    List<String> stringUserList;
    FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_requests_fragment,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewAllSignUpRequests=(ListView)view.findViewById(R.id.listViewSignUpRequests);
        userList=new ArrayList<>();
        stringUserList=new ArrayList<>();
        db=FirebaseDatabase.getInstance();

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stringUserList);
        listViewAllSignUpRequests.setAdapter(adapter);

        listViewAllSignUpRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showListViewItemClickAlertDialog(userList.get(i));
            }
        });

        db.getReference("sign_up_requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                stringUserList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user=ds.getValue(User.class);
                    userList.add(user);
                    Toast.makeText(getActivity(),String.valueOf(user.getUserUid())+" istek var!",Toast.LENGTH_SHORT).show();

                    stringUserList.add(user.getUserEmail());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
            }
        });
    }

    private void showListViewItemClickAlertDialog(final User _user){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabase myDatabase=new MyDatabase();
                _user.setAdmin(false);
                myDatabase.addUserAdmin(getActivity(),_user);

                SendEmail _sendEmail=new SendEmail(getActivity());
                _sendEmail.sendMail(_user.getUserEmail(),"Kayıt İsteği","Kayıt isteğiniz kabul edildi.");
                myDatabase.deleteUser(_user);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabase myDatabase=new MyDatabase();
                myDatabase.deleteUser(_user);
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
