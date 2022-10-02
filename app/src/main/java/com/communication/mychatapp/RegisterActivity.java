package com.communication.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.communication.mychatapp.objectclasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {

    private Button buttonSignIn;
    private Button buttonSignUp;
    private Button buttonCompleteRegistration;
    private Spinner spinnerPosition;
    private FirebaseAuth mAuth;
    private EditText editTextName;
    private EditText editTextNickName;
    private EditText editTextMail;
    private EditText editTextPassword;
    private EditText editTextPasswordVerification;
    private String name;
    private String nickName;
    private String mail;
    private String password;
    private String passwordVerification;

    private static final String TAG = "Error";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinnerPosition = (Spinner) findViewById(R.id.spinner_position_register);
        buttonSignIn = (Button) findViewById(R.id.button_sign_in_register);
        buttonSignUp = (Button) findViewById(R.id.button_sign_up_register);
        buttonCompleteRegistration = (Button) findViewById(R.id.buttonCompleteRegistration);
        editTextName = (EditText) findViewById(R.id.edittext_name_register);
        editTextNickName = (EditText) findViewById(R.id.edittext_nickname_register);
        editTextMail = (EditText) findViewById(R.id.edittext_mail_register);
        editTextPassword = (EditText) findViewById(R.id.edittex_password_register);
        editTextPasswordVerification = (EditText) findViewById(R.id.edittex_password_verification_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        buttonCompleteRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                user.reload();
                User _user = new User();
                _user.setUserEmail(user.getEmail());
                _user.setUserCreationTimeStamp(user.getMetadata().getCreationTimestamp());
                _user.setUserName(name);
                _user.setUserUid(user.getUid());

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                if (user.isEmailVerified()) {
                    db.getReference("sign_up_requests").child(mAuth.getCurrentUser().getUid()).setValue(_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showAlertDialog(R.string.congrulation,
                                        R.string.completed_the_registration,
                                        getString(R.string.alert_dialog_positive_button_OK),
                                        LoginActivity.class,
                                        true);
                                mAuth.signOut();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                } else {
                    showAlertDialogMailVerification(user, R.string.email_doesnt_verifiy_send_again);
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editTextName.getText().toString().trim();
                nickName = editTextNickName.getText().toString().trim();
                mail = editTextMail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                passwordVerification = editTextPasswordVerification.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), R.string.edittext_name_not_null, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(nickName)) {
                    Toast.makeText(getApplicationContext(), R.string.edittext_nickname_not_null, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplicationContext(), R.string.edittext_mail_not_null, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.edittext_password_not_null, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passwordVerification)) {
                    Toast.makeText(getApplicationContext(), R.string.edittext_password_verification_not_null, Toast.LENGTH_SHORT).show();
                } else if (!password.equals(passwordVerification)) {
                    Toast.makeText(getApplicationContext(), R.string.password_verification_error, Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(mail, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final FirebaseUser user = mAuth.getCurrentUser();
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    showAlertDialog(R.string.congrulation,
                                                            R.string.alert_dialog_message_registrationIsOK_and_mail_verification_sent,
                                                            getString(R.string.alert_dialog_positive_button_OK), null, false);
                                                } else {
                                                    showAlertDialogMailVerification(user, R.string.alert_dialog_message_registrationIsOK_and_mail_verification_not_sent);
                                                }
                                            }
                                        });
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<String> listPosition = new ArrayList<>();
        listPosition.add("Öğrenci");
        listPosition.add("Veli");
        listPosition.add("Öğretmen");
        listPosition.add("Yönetici");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listPosition);
        spinnerAdapter.setDropDownViewResource(R.layout.row_spinner_position_register);
        spinnerPosition.setAdapter(spinnerAdapter);
    }

    private void showAlertDialog(int dialogTitle, int dialogMessage, String positiveButtonText, final Class<?> aClass, final boolean isStartActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMessage);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isStartActivity) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(getApplicationContext(), aClass);
                    startActivity(intent);
                    finish();
                } else {
                    dialogInterface.dismiss();
                    buttonCompleteRegistration.setVisibility(View.VISIBLE);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showAlertDialogMailVerification(final FirebaseUser user, int dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(dialogMessage);
        builder.setCancelable(false);
        builder.setPositiveButton(getText(R.string.button_send_verification_mail), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.sent_verification_mail, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            dialogInterface.dismiss();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.not_sent_verification_mail, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}