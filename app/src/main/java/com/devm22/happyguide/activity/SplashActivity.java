package com.devm22.happyguide.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devm22.happyguide.Config;
import com.devm22.happyguide.R;
import com.devm22.happyguide.utils.Shared;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private ImageView appIcon;
    private TextView appName;
    private ProgressBar progressBarLoading;

    private int progressBarCount = 0;

    private Shared shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appIcon = findViewById(R.id.appIcon);
        appName = findViewById(R.id.appName);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        shared = new Shared(this);

        if (shared.getBoolean(Config.MODE_NIGHT_YES, true)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }


        buildSplash();


    }


    private void buildSplash(){
        progressBarCount = 0;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    for (int i=0; i<100; i++){
                        sleep(50);

                        progressBarCount++;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBarLoading.setProgress(progressBarCount);
                            }
                        });
                    }



                }catch (Exception e){

                }

                finally {
                    open();
                }


            }
        };
        thread.start();

    }


    private void open() {
        Intent intent = new Intent(this, FirstActivity.class);
        intent.putExtra(Config.INTENT_RATING, true);
        startActivity(intent);
        finish();
    }





}