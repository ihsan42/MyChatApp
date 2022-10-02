package com.communication.mychatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.communication.mychatapp.adapters.PageAdapterForAdminPanel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminPanel extends AppCompatActivity {

    ViewPager viewPager;
    PageAdapterForAdminPanel tabPageAdapter;
    Button buttonSignOut;
    Button buttonOpenUsersPanel;
    FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        buttonSignOut = (Button) findViewById(R.id.buttonSignOut_admin_panel);
        buttonOpenUsersPanel=(Button)findViewById(R.id.button_open_users_panel);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Bundle bundle = new Bundle();
        bundle.putString("screen", "admin_panel");
        bundle.putString("userUid", user.getUid());
        bundle.putString("userMail", user.getEmail());
        bundle.putString("userSignInTime", String.valueOf(user.getMetadata().getLastSignInTimestamp()));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        viewPager = (ViewPager) findViewById(R.id.view_pager_admin_panel);
        tabPageAdapter = new PageAdapterForAdminPanel(getSupportFragmentManager());
        viewPager.setAdapter(tabPageAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_admin_panel);
        tabLayout.setupWithViewPager(viewPager);

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonOpenUsersPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
