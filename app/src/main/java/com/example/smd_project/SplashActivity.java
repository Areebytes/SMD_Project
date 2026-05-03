package com.example.smd_project;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.content.Intent;


public class SplashActivity extends AppCompatActivity{

    ImageView splashLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashLogo =findViewById(R.id.sLogo);
        splashLogo.setAlpha(0f); // Start invisible
        splashLogo.animate().alpha(1f).setDuration(1500); // Fade in over 1.5 seconds



        new Handler().postDelayed(()->{
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            finish();
        }, 5000);

    }
}
