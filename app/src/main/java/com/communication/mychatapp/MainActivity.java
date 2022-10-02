package com.communication.mychatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.communication.mychatapp.adapters.PageAdapterForMain;
import com.communication.mychatapp.services.MessagingService;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    PageAdapterForMain tabPageAdapter;
    Button buttonSignOut;
    FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        buttonSignOut=(Button)findViewById(R.id.buttonSignOut_main);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();

        Bundle bundle = new Bundle();
        bundle.putString("screen","anasayfa" );
        bundle.putString("userUid", user.getUid());
        bundle.putString("userMail", user.getEmail());
        bundle.putString("userSignInTime",String.valueOf(user.getMetadata().getLastSignInTimestamp()));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        viewPager = (ViewPager) findViewById(R.id.view_pager_main);
        tabPageAdapter = new PageAdapterForMain(getSupportFragmentManager());
        viewPager.setAdapter(tabPageAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_main);
        tabLayout.setupWithViewPager(viewPager);

        startMessagingService();
        Toast.makeText(getApplicationContext(),user.getEmail(),Toast.LENGTH_LONG).show();

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startMessagingService(){
        Intent serviceIntent = new Intent(MainActivity.this,MessagingService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

            MainActivity.this.startForegroundService(serviceIntent);
        }else{
            startService(serviceIntent);
        }
    }

    private void stopMessagingService() {
        Intent serviceIntent = new Intent(MainActivity.this,MessagingService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

            MainActivity.this.stopService(serviceIntent);
        }else{
            stopService(serviceIntent);
        }
    }
}
