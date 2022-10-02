package com.communication.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.communication.mychatapp.objectclasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Button buttonSingUp;
    private Button buttonSingIn;
    private CheckBox checkBoxRememberMe;
    private EditText editTextMail;
    private EditText editTextPassword;
    private Button buttonForgotPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        buttonSingUp = (Button) findViewById(R.id.button_sign_up_login);
        buttonSingIn = (Button) findViewById(R.id.button_sign_in_login);
        buttonForgotPassword = (Button) findViewById(R.id.button_forgot_password_login);
        editTextMail = (EditText) findViewById(R.id.edittex_mail_login);
        editTextPassword = (EditText) findViewById(R.id.edittex_password_login);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkbox_remember_me_login);

        new getAllUsers().execute();

        buttonSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = editTextMail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplicationContext(), R.string.edittext_mail_not_null, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.edittext_password_not_null, Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();

                                new getAllUsers().execute();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(R.string.error);
                                builder.setMessage(task.getException().getLocalizedMessage());
                                builder.setCancelable(false);
                                builder.setPositiveButton(R.string.alert_dialog_positive_button_OK, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                    });
                }
            }
        });

        buttonSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class getAllUsers extends AsyncTask<Void, Void, List<User>> {
        ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Bağlanılıyor...");
            progressDialog.show();

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            db=FirebaseDatabase.getInstance();
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            final boolean[] isDbHasUser = {false};
            final List<User> userList=new ArrayList<>();
            final List<String> signUpRequestList=new ArrayList<>();
            if(user!=null){
                db.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User _user = ds.getValue(User.class);
                            userList.add(_user);
                        }

                        if(userList.size()!=0){
                            boolean isAdmin=false;
                            String username="";
                            for (User _user : userList) {
                                if (_user.getUserUid().equals(user.getUid())) {
                                    isDbHasUser[0] = true;
                                    if(_user.isAdmin()==true){
                                        isAdmin=true;
                                    }
                                    username=_user.getUserName();
                                }
                            }

                            if (isDbHasUser[0] == true) {
                                MyDatabase myDatabase = new MyDatabase();
                                myDatabase.updateUser(user,username,isAdmin);

                                if(isAdmin==true){
                                    Intent intent=new Intent(getApplicationContext(),AdminPanel.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            if(isDbHasUser[0]==false){
                                db.getReference("sign_up_requests").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                                            signUpRequestList.add(ds.getKey());
                                        }

                                        Log.e("signUpRequests", String.valueOf(signUpRequestList.size()));

                                        boolean isDbHasSignUpRequest=false;
                                        for (String _uid : signUpRequestList) {
                                            if (_uid.equals(user.getUid())) {
                                                isDbHasSignUpRequest = true;
                                            }
                                        }

                                        Log.e("isDbHasSignUpRequest", String.valueOf(isDbHasSignUpRequest));

                                        if (isDbHasSignUpRequest== true) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setTitle(R.string.error);
                                            builder.setMessage(R.string.admin_is_not_verify_your_account);
                                            builder.setCancelable(false);
                                            builder.setPositiveButton(R.string.alert_dialog_positive_button_OK, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }else if(isDbHasUser[0]==false){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setTitle(R.string.error);
                                            builder.setMessage(R.string.you_are_missing_in_records);
                                            builder.setCancelable(false);
                                            builder.setPositiveButton(R.string.alert_dialog_positive_button_OK, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            return userList;
        }

        @Override
        protected void onPostExecute(List<User> listUser) {
            super.onPostExecute(listUser);
            progressDialog.dismiss();
        }
    }
}