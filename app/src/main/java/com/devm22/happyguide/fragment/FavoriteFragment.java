package com.devm22.happyguide.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.devm22.happyguide.Config;
import com.devm22.happyguide.R;
import com.devm22.happyguide.activity.ContentActivity;
import com.devm22.happyguide.adapter.TipsAdapter;
import com.devm22.happyguide.helper.DatabaseHelperTips;
import com.devm22.happyguide.model.Tips;
import com.devm22.happyguide.utils.MyAdmobAd;
import com.devm22.happyguide.utils.Shared;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RecyclerView recyclerViewTips;
    private TextView textNoFavoriteTips;

    Shared shared;

    private DatabaseHelperTips databaseHelperTips;
    private TipsAdapter tipsAdapter;
    private ArrayList<Tips> tipsArrayList = new ArrayList<>();
    private ArrayList<Object> mTips = new ArrayList<>();

    private int positionClicked;
    private boolean nativeAdsIsRunning = false;


    private MyAdmobAd myAdmobAd;
    private ArrayList<NativeAd> nativeAdList =new ArrayList<>();
    private int native_count = 0;
    private int native_number_of_ads = 0;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerViewTips = view.findViewById(R.id.recycleViewTips);
        textNoFavoriteTips = view.findViewById(R.id.textNoFavoriteTips);


        shared = new Shared(getContext());
        databaseHelperTips = new DatabaseHelperTips(getContext());



        buildRecycleViewTips();
        loadData();
        initAds();


        return view;
    }

    private void buildRecycleViewTips(){
        recyclerViewTips.setHasFixedSize(true);
        recyclerViewTips.setLayoutManager(new LinearLayoutManager(getContext()));
        tipsAdapter = new TipsAdapter(getContext());
        recyclerViewTips.setAdapter(tipsAdapter);

        recyclerViewTips.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        recyclerViewTips.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < recyclerViewTips.getChildCount(); i++) {
                            View v = recyclerViewTips.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .setStartDelay(i * 50)
                                    .start();
                        }

                        return true;
                    }
                });

        tipsAdapter.setOnItemClickListener(new TipsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                positionClicked = position;
                if (myAdmobAd.is_interstitial_ad_loaded()){
                    myAdmobAd.show_interstitial_ad();
                }else {
                    goToContent(positionClicked);
                }

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

                loadData();
            }

            @Override
            public void onRateChangedClick(int positionRecycle, Tips tips, float rate) {
                tips.setTipRate(rate);
                databaseHelperTips.updateTipsRate(tips.getTipId(), rate);

            }
        });


    }

    private void goToContent(int pos){
        Intent intent = new Intent(getContext(), ContentActivity.class);
        intent.putExtra("POSITION", pos);
        startActivity(intent);
    }

    private void loadData() {
        if (tipsAdapter != null){
            tipsArrayList = new ArrayList<>();

            tipsArrayList = databaseHelperTips.getFavoriteTips();



            mTips = new ArrayList<>();
            mTips.addAll(tipsArrayList);
            tipsAdapter.addTips(mTips);

            if (tipsArrayList.size() == 0){
                textNoFavoriteTips.setVisibility(View.VISIBLE);
            }else {
                textNoFavoriteTips.setVisibility(View.GONE);
            }

            native_number_of_ads = mTips.size() / Config.FreePlaceForNativeAd;

            if (tipsArrayList.size() > 1){
                if (Config.ActiveNativeAds){
                    createNativeAd();

                }
            }


        }
    }

    // Ads
    private void initAds() {
        myAdmobAd = new MyAdmobAd(getActivity(), new MyAdmobAd.CallbackInitialization() {
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
    }


    private void createInterAd() {
        myAdmobAd.createInterstitial(new MyAdmobAd.CallbackInterstitial() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                goToContent(positionClicked);
            }

            @Override
            public void onAdFailedToLoad(String error) {
                Log.e(TAG, "onAdFailedToLoad Inter: " + error);

            }
        });

    }

    private void createNativeAd() {
        if (!nativeAdsIsRunning){
            nativeAdsIsRunning = true;

            AdLoader adLoader = new AdLoader.Builder(getActivity(), getActivity().getResources().getString(R.string.google_ads_native_id))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd mNative) {
                            native_count ++;
                            nativeAdList.add(mNative);
                            if(native_count == native_number_of_ads) {
                                tipsAdapter.addNativeAds(nativeAdList);
                                tipsAdapter.mixNativeWithTips();
                                native_count = 0;
                            }

                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            Log.e(TAG, "onAdFailedToLoad: " + adError);
                            native_count ++;
                            if(native_count == native_number_of_ads) {
                                tipsAdapter.mixNativeWithTips();
                                native_count = 0;
                            }
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(new com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();

            adLoader.loadAds(new AdRequest.Builder().build(), native_number_of_ads);


        }else {
            if (nativeAdList.size() == native_number_of_ads){
                tipsAdapter.addNativeAds(nativeAdList);
                tipsAdapter.mixNativeWithTips();
                native_count = 0;
            }
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        myAdmobAd.onMainPause();
    }

    @Override
    public void onDestroy() {
        myAdmobAd.onMainDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        myAdmobAd.onMainResume();

    }
}