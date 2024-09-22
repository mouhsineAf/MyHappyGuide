package com.devm22.happyguide.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.devm22.happyguide.Config;
import com.devm22.happyguide.R;
import com.devm22.happyguide.utils.MyAdmobAd;
import com.devm22.happyguide.utils.Utils;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "FirstActivity";

    private Button btnStart, btnShare, btnRate;
    private FrameLayout frameNativeAd;

    private MyAdmobAd myAdmobAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnStart = findViewById(R.id.btnStart);
        btnShare = findViewById(R.id.btnShare);
        btnRate = findViewById(R.id.btnRate);
        frameNativeAd = findViewById(R.id.frame_native_ad);

        onClick();


        initAds();
        showDialogRating();

    }

    private void onClick(){
        btnStart.setOnClickListener(view -> {
            setAnim(btnStart);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    start();
                }
            }, 330);

        });


        btnShare.setOnClickListener(view -> {
            setAnim(btnShare);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Utils.shareApp(FirstActivity.this);
                }
            }, 330);
        });

        btnRate.setOnClickListener(view -> {
            setAnim(btnRate);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Utils.rateApp(FirstActivity.this);
                }
            }, 330);
        });

    }

    private void setAnim(Button button){
        Animation animationUtils = AnimationUtils.loadAnimation(this, R.anim.click);
        button.startAnimation(animationUtils);
    }


    private void start(){
        if (myAdmobAd.is_interstitial_ad_loaded()){
            myAdmobAd.show_interstitial_ad();
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    private void showDialogRating() {
        if (getIntent().getBooleanExtra(Config.INTENT_RATING, false)){
            if (!Utils.isShowingDialogRating(this)){
                Utils.showDialogRating(FirstActivity.this);
            }
        }
    }


    // Ads
    private void initAds() {
        myAdmobAd = new MyAdmobAd(FirstActivity.this, new MyAdmobAd.CallbackInitialization() {
            @Override
            public void onInitializationComplete() {
                Log.e(TAG, "onInitializationComplete: ");

            }

            @Override
            public void onInitializationFailed(String error) {
                Log.e(TAG, "onInitializationFailed: " + error);

            }
        });
        if (Config.ActiveNativeAds){
            createNativeAd();

        }
        if (Config.ActiveInterstitialAds){
            createInterAd();

        }
    }


    private void createInterAd() {
        myAdmobAd.createInterstitial(new MyAdmobAd.CallbackInterstitial() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAdFailedToLoad(String error) {
                Log.e(TAG, "onAdFailedToLoad Inter: " + error);

            }
        });

    }

    private void createNativeAd() {
        myAdmobAd.createNative(new MyAdmobAd.CallbackNative() {
            @Override
            public void onAdLoaded(View nativeAdView) {
                frameNativeAd.removeAllViews();
                frameNativeAd.addView(nativeAdView);
            }

            @Override
            public void onAdFailedToLoad(String error) {
                Log.e(TAG, "onAdFailedToLoad native: " + error);

            }
        });

        myAdmobAd.show_native_ad(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        myAdmobAd.onMainResume();
    }

    @Override
    protected void onDestroy() {
        myAdmobAd.onMainDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myAdmobAd.onMainPause();
    }
}