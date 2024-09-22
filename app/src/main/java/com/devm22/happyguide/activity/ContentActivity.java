package com.devm22.happyguide.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devm22.happyguide.Config;
import com.devm22.happyguide.R;
import com.devm22.happyguide.adapter.TipsAdapter;
import com.devm22.happyguide.helper.DatabaseHelperTips;
import com.devm22.happyguide.model.Tips;
import com.devm22.happyguide.utils.MyAdmobAd;
import com.devm22.happyguide.utils.Shared;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity {
    private static final String TAG = "ContentActivity";

    private ImageView tipsImage;
    private RelativeLayout layoutTipsImage;
    private TextView tipsName;
    private TextView tipsContent;
    private AppCompatRatingBar tipsRate;
    private ImageView tipsFavorite;
    private ProgressBar progressBarLoadingIcon;
    private ImageView btnBack;
    private TextView textViewAll;

    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private ImageView header_Arrow_Image;

    private DatabaseHelperTips databaseHelperTips;
    private Tips tips;
    private ArrayList<Tips> tipsArrayList = new ArrayList<>();

    private RecyclerView recyclerViewTips;
    private TipsAdapter tipsAdapter;
    private ArrayList<Object> mTips = new ArrayList<>();

    private int positionClicked;
    private int positionLive;

    private Shared shared;

    private MyAdmobAd myAdmobAd;
    private FrameLayout frameLayoutBannerAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        mBottomSheetLayout = findViewById(R.id.layout_bottom);
        header_Arrow_Image = findViewById(R.id.bottom_sheet_arrow);
        tipsImage = findViewById(R.id.tips_image);
        btnBack = findViewById(R.id.btn_back);
        textViewAll = findViewById(R.id.text_view_all);
        layoutTipsImage = findViewById(R.id.layout_tips_image);
        tipsName = findViewById(R.id.tips_name);
        tipsContent = findViewById(R.id.tips_content);
        tipsRate = findViewById(R.id.tips_rate);
        tipsFavorite = findViewById(R.id.tips_favorite);
        progressBarLoadingIcon = findViewById(R.id.progress_icon_loading);
        recyclerViewTips = findViewById(R.id.recycleViewAnotherTips);
        frameLayoutBannerAd = findViewById(R.id.frameBannerAd);

        databaseHelperTips = new DatabaseHelperTips(this);
        shared = new Shared(this);


        getDataFromIntent();
        initAds();


        btnBack.setOnClickListener(view -> {
            if (myAdmobAd.is_interstitial_ad_loaded()){
                myAdmobAd.show_interstitial_ad();
            }else {
                back();
            }
        });

        textViewAll.setOnClickListener(view -> {
            if (myAdmobAd.is_interstitial_ad_loaded()){
                myAdmobAd.show_interstitial_ad();
            }else {
                back();
            }
        });

    }

    private void getDataFromIntent(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tipsArrayList = databaseHelperTips.getTipsByDate(true);
            positionLive = bundle.getInt("POSITION");
            tips = tipsArrayList.get(positionLive);

            bottomSheetDialog();

            notifyDataSetChanged();

        }
    }

    private void bottomSheetDialog(){
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        sheetBehavior.setHideable(false);


        int peekHeightPx = (int) (getResources().getDisplayMetrics().heightPixels - 670);
        sheetBehavior.setPeekHeight(peekHeightPx);

        // build Recycle View
        buildRecycleViewTips();

        // Set data
        String imageUrl = tips.getTipImage();
        if (!imageUrl.isEmpty()){
            if (imageUrl.contains("https://") || imageUrl.contains("http://")){
                progressBarLoadingIcon.setVisibility(VISIBLE);
                Picasso.get()
                        .load(imageUrl)
                        .into(tipsImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarLoadingIcon.setVisibility(GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

            }else {
                tipsImage.setImageResource(Integer.parseInt(imageUrl));
            }
        }

        tipsName.setText(tips.getTipName());
        tipsContent.setText(tips.getTipContent());
        tipsContent.setTextSize(shared.getFloat(Config.SharedTextContentSize, getResources().getDimension(R.dimen.content_text_size_moy)));

        tipsRate.setRating(tips.getTipRate());

        if (tips.isTipFavorite()){
            tipsFavorite.setImageResource(R.drawable.ic_favorite_on);
        }else {
            tipsFavorite.setImageResource(R.drawable.ic_favorite_off);
        }



        //On Click
        tipsFavorite.setOnClickListener(view -> {
            if (tips.isTipFavorite()){
                tips.setTipFavorite(false);
                databaseHelperTips.updateTipsFavorite(tips.getTipId(), 0);
                tipsFavorite.setImageResource(R.drawable.ic_favorite_off);
            }else {
                tips.setTipFavorite(true);
                databaseHelperTips.updateTipsFavorite(tips.getTipId(), 1);
                tipsFavorite.setImageResource(R.drawable.ic_favorite_on);

            }
        });

        tipsRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tips.setTipRate(ratingBar.getRating());
                databaseHelperTips.updateTipsRate(tips.getTipId(), ratingBar.getRating());
            }
        });


        header_Arrow_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


                }

            }
        });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HALF_EXPANDED){

                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){


                }

            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                header_Arrow_Image.setRotation(slideOffset * 180);
            }
        });




    }

    private void buildRecycleViewTips(){
        recyclerViewTips.setHasFixedSize(true);
        recyclerViewTips.setLayoutManager(new LinearLayoutManager(ContentActivity.this));
        tipsAdapter = new TipsAdapter(ContentActivity.this);
        recyclerViewTips.setAdapter(tipsAdapter);

        tipsAdapter.setOnItemClickListener(new TipsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                positionClicked = position;
                goToContent(position);

            }

            @Override
            public void onFavoriteClick(Tips tips, int position) {
                if (tips.isTipFavorite()){
                    tips.setTipFavorite(false);
                    databaseHelperTips.updateTipsFavorite(tips.getTipId(), 0);
                }else {
                    tips.setTipFavorite(true);
                    databaseHelperTips.updateTipsFavorite(tips.getTipId(), 1);
                }

                notifyDataSetChanged();

            }

            @Override
            public void onRateChangedClick(int positionRecycle, Tips tips, float rate) {
                tips.setTipRate(rate);
                databaseHelperTips.updateTipsRate(tips.getTipId(), rate);

            }
        });

    }

    private void goToContent(int pos){
        Intent intent = new Intent(ContentActivity.this, ContentActivity.class);
        intent.putExtra("POSITION", pos);
        startActivity(intent);
        finish();
    }


    private void notifyDataSetChanged(){
        if (tipsAdapter != null){
            mTips = new ArrayList<>();

            if (tipsArrayList.size() >= 3){

                for (int i=positionLive+1; i<tipsArrayList.size(); i++){

                    if (mTips.size() < 3){
                        mTips.add(tipsArrayList.get(i));

                    }else {
                        break;
                    }

                }

                if (mTips.size() < 3){
                    for (int i=0; i<tipsArrayList.size(); i++){

                        if (mTips.size() < 3){
                            mTips.add(tipsArrayList.get(i));

                        }else {
                            break;
                        }

                    }
                }
            }else {
                mTips.addAll(tipsArrayList);

            }

            tipsAdapter.addTips(mTips);

        }
    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        if (myAdmobAd.is_interstitial_ad_loaded()){
            myAdmobAd.show_interstitial_ad();
        }else {
            back();
        }

    }

    private void back(){
        finish();
    }


    // Ads
    private void initAds() {
        myAdmobAd = new MyAdmobAd(ContentActivity.this, new MyAdmobAd.CallbackInitialization() {
            @Override
            public void onInitializationComplete() {
                Log.e(TAG, "onInitializationComplete: ");

            }

            @Override
            public void onInitializationFailed(String error) {
                Log.e(TAG, "onInitializationFailed: " + error);

            }
        });

        if (Config.ActiveInterstitialAds){
            createInterAd();
        }

        if (Config.ActiveBannerAds){
            createBannerAd();
        }

    }


    private void createInterAd() {
        myAdmobAd.createInterstitial(new MyAdmobAd.CallbackInterstitial() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                back();
            }

            @Override
            public void onAdFailedToLoad(String error) {
                Log.e(TAG, "onAdFailedToLoad Inter: " + error);

            }
        });

    }

    private void createBannerAd() {
        myAdmobAd.createBanner(new MyAdmobAd.CallbackBanner() {
            @Override
            public void onAdLoaded(View adView) {
                frameLayoutBannerAd.removeAllViews();
                frameLayoutBannerAd.addView(adView);
            }

            @Override
            public void onAdFailedToLoad(String error) {

            }
        });
        myAdmobAd.show_banner_ad(true);

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