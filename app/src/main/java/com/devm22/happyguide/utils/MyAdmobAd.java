package com.devm22.happyguide.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.devm22.happyguide.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class MyAdmobAd {
    private static final String TAG = "MyAdmobAd";
    Activity activity = null;

    AdView bannerAdView;
    InterstitialAd mInterstitialAd;
    NativeAd nativeAd;



    public MyAdmobAd(Activity activity, CallbackInitialization callbackInitialization) {
        this.activity = activity;

        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                callbackInitialization.onInitializationComplete();
            }
        });


    }

    public void createBanner(CallbackBanner callbackBanner) {
        bannerAdView = new AdView(activity);
        bannerAdView.setAdSize(AdSize.BANNER);
        bannerAdView.setAdUnitId(activity.getResources().getString(R.string.google_ads_banner_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);

        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                callbackBanner.onAdFailedToLoad(loadAdError.toString());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                callbackBanner.onAdLoaded(bannerAdView);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
    }

    public void show_banner_ad(final boolean show) {
        if (bannerAdView == null) { return; }

        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (show) {
                    if (bannerAdView.isEnabled()) {
                        bannerAdView.setEnabled(true);
                    }
                    if (bannerAdView.getVisibility() == View.INVISIBLE) {
                        bannerAdView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (bannerAdView.isEnabled()) { bannerAdView.setEnabled(false); }
                    if (bannerAdView.getVisibility() != View.INVISIBLE) {
                        bannerAdView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    public void createNative(CallbackNative callbackNative){
        AdLoader adLoader = new AdLoader.Builder(activity, activity.getResources().getString(R.string.google_ads_native_id))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd mNative) {
                        // Show the ad.
                        Log.e(TAG, "onNativeAdLoaded");
                        nativeAd = mNative;

                        // Assumes that your ad layout is in a file call native_ad_layout.xml
                        // in the res/layout folder
                        NativeAdView adView = (NativeAdView) activity.getLayoutInflater()
                                .inflate(R.layout.layout_native_ad_admob, null);


                        // This method sets the text, images and the native ad, etc into the ad
                        // view.
                        populateNativeAdView(nativeAd, adView);

                        callbackNative.onAdLoaded(adView);                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.e(TAG, "onAdFailedToLoad" + adError);
                        callbackNative.onAdFailedToLoad(adError.toString());
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        //  NativeAdView linearNativeCard = adView.findViewById(R.id.ad_view);

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    public void show_native_ad(final boolean show) {
        if (nativeAd == null) { return; }
    }

    public void createInterstitial(CallbackInterstitial callbackInterstitial) {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, activity.getResources().getString(R.string.google_ads_interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd = interstitialAd;
                        callbackInterstitial.onAdLoaded();


                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                callbackInterstitial.onAdClosed();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                        callbackInterstitial.onAdFailedToLoad(loadAdError.toString());
                    }
                });

    }



    public void show_interstitial_ad () {
        if (mInterstitialAd == null) { return; }

        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(activity);
                    mInterstitialAd = null;
                } else {
                    Log.d(TAG, "The interstitial ad wasn't ready yet.");
                }

            }
        });
    }


    public boolean is_interstitial_ad_loaded() {
        return mInterstitialAd != null;
    }



    //interfaces
    public interface CallbackInitialization {
        void onInitializationComplete();
        void onInitializationFailed(String error);
    }

    public interface CallbackBanner {
        void onAdLoaded(View adView);
        void onAdFailedToLoad(String error);
    }

    public interface CallbackNative {
        void onAdLoaded(View nativeAdView);
        void onAdFailedToLoad(String error);
    }

    public interface CallbackInterstitial {
        void onAdLoaded();
        void onAdClosed();
        void onAdFailedToLoad(String error);
    }


    public void onMainPause() {
        if (bannerAdView != null){
            bannerAdView.pause();
        }
    }

    public void onMainResume() {
        if (bannerAdView != null){
            bannerAdView.resume();
        }

    }

    public void onMainDestroy() {
        if (bannerAdView != null){
            bannerAdView.destroy();
        }
        if (nativeAd != null){
            nativeAd.destroy();
        }
    }


}
