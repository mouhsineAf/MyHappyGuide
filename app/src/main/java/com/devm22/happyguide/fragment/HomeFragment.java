package com.devm22.happyguide.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;



import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devm22.happyguide.Config;
import com.devm22.happyguide.R;
import com.devm22.happyguide.activity.ContentActivity;
import com.devm22.happyguide.activity.NotificationsActivity;
import com.devm22.happyguide.adapter.TipsAdapter;
import com.devm22.happyguide.helper.DatabaseHelperNotifications;
import com.devm22.happyguide.helper.DatabaseHelperTips;
import com.devm22.happyguide.model.Tips;
import com.devm22.happyguide.utils.MyAdmobAd;
import com.devm22.happyguide.utils.Shared;
import com.devm22.happyguide.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ImageView imageBtnNotifications;
    private EditText editTextSearch;
    private ImageView imageFilter;
    private RecyclerView recyclerViewTips;
    private ProgressBar progressBarLoadingTips;

    private MyAdmobAd myAdmobAd;
    private ArrayList<NativeAd> nativeAdList =new ArrayList<>();
    private int native_count = 0;
    private int native_number_of_ads = 0;
    private boolean nativeAdsIsRunning = false;

    Shared shared;

    private DatabaseHelperTips databaseHelperTips;
    private TipsAdapter tipsAdapter;
    private ArrayList<Tips> tipsArrayList = new ArrayList<>();
    private ArrayList<Object> mTips = new ArrayList<>();

    private int positionClicked;

    private DatabaseHelperNotifications databaseHelperNotifications;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imageBtnNotifications = view.findViewById(R.id.btn_notifications);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        imageFilter = view.findViewById(R.id.image_filter);
        recyclerViewTips = view.findViewById(R.id.recycleViewTips);
        progressBarLoadingTips = view.findViewById(R.id.progress_tips_loading);


        shared = new Shared(getContext());
        databaseHelperTips = new DatabaseHelperTips(getContext());
        databaseHelperNotifications = new DatabaseHelperNotifications(getContext());



        onClick();
        buildRecycleViewTips();
        loadData();
        initAds();



        return view;
    }

    private void onClick() {
        imageBtnNotifications.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), NotificationsActivity.class);
            startActivity(intent);
        });
    }

    private void checkNotifications() {
        if (databaseHelperNotifications.CheckForNewNotifications()){
            imageBtnNotifications.setImageResource(R.drawable.ic_notification_on);
        }else {
            imageBtnNotifications.setImageResource(R.drawable.ic_notification_off);
        }
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
                editTextSearch.setText("");
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

                notifyDataSetChanged();
            }

            @Override
            public void onRateChangedClick(int positionRecycle, Tips tips, float rate) {
              //  Tips tips = (Tips) mTips.get(positionRecycle);
                tips.setTipRate(rate);
                databaseHelperTips.updateTipsRate(tips.getTipId(), rate);

            }
        });


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tipsAdapter.search(editTextSearch.getText().toString());

            }
        });


        imageFilter.setOnClickListener(view -> {
            showDialogSort();
        });


    }

    private void goToContent(int pos){
        Intent intent = new Intent(getContext(), ContentActivity.class);
        intent.putExtra("POSITION", pos);
        startActivity(intent);
    }


    private void loadData(){
        progressBarLoadingTips.setVisibility(View.VISIBLE);

        try {
            JSONObject jsonObj = new JSONObject(Utils.loadJSONFromAsset(getActivity()));
            JSONArray mData = jsonObj.getJSONArray("tips");

            for (int i = 0; i < mData.length(); i++) {
                JSONObject c = mData.getJSONObject(i);

                int tipsId = c.getInt("id");
                String tipsName = c.getString("name");
                String tipsImage = c.getString("image");
                String tipsContent = c.getString("content");


                if (!databaseHelperTips.CheckIsDataAlreadyInDBorNot(tipsId)){
                    databaseHelperTips.addTips(tipsId, tipsName, tipsImage, tipsContent, Utils.dateToString(Utils.getDate(i)), Utils.randFloat(1, 5), 0);
                }else {
                    databaseHelperTips.updateTips(tipsId, tipsName, tipsImage, tipsContent);
                }

            }

            notifyDataSetChanged();

            progressBarLoadingTips.setVisibility(View.GONE);


        } catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
            progressBarLoadingTips.setVisibility(View.GONE);
            if (Utils.getConnectionType(getContext()) == 0){
                if (databaseHelperTips.getTipsByDate(true).size() == 0){
                    showDialogNoInternet();
                }

            }
        }

    }

    private void showDialogSort() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_sort);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RadioGroup radioGroupFilter = dialog.findViewById(R.id.radioGroupFilter);
        RadioButton radioNewDate = dialog.findViewById(R.id.radioNewDate);
        RadioButton radioLastDate = dialog.findViewById(R.id.radioLastDate);
        RadioButton radioBestRate = dialog.findViewById(R.id.radioBestRate);
        RadioButton radioLowRate = dialog.findViewById(R.id.radioLowRate);

        ((RadioButton)radioGroupFilter.getChildAt(shared.getInt(Config.SortModeTips, 0))).setChecked(true);

        radioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radioNewDate.getId()){
                    shared.putInt(Config.SortModeTips, 0);
                    notifyDataSetChanged();

                }
                if (i == radioLastDate.getId()){
                    shared.putInt(Config.SortModeTips, 1);
                    notifyDataSetChanged();

                }
                if (i == radioBestRate.getId()){
                    shared.putInt(Config.SortModeTips, 2);
                    notifyDataSetChanged();

                }
                if (i == radioLowRate.getId()){
                    shared.putInt(Config.SortModeTips, 3);
                    notifyDataSetChanged();

                }
                dialog.dismiss();

            }
        });



        dialog.show();
    }

    private void notifyDataSetChanged(){
        if (tipsAdapter != null){
            tipsArrayList = new ArrayList<>();
            int sortMode = shared.getInt(Config.SortModeTips, 0);
            switch (sortMode) {
                case 0:
                    tipsArrayList = databaseHelperTips.getTipsByDate(true);
                    break;
                case 1:
                    tipsArrayList = databaseHelperTips.getTipsByDate(false);
                    break;
                case 2:
                    tipsArrayList = databaseHelperTips.getTipsByRate(true);
                    break;
                case 3:
                    tipsArrayList = databaseHelperTips.getTipsByRate(false);
                    break;
            }


            mTips = new ArrayList<>();
            mTips.addAll(tipsArrayList);
            tipsAdapter.addTips(mTips);

            native_number_of_ads = mTips.size() / Config.FreePlaceForNativeAd;

            if (tipsArrayList.size() > 1){
                if (Config.ActiveNativeAds){
                    createNativeAd();

                }

            }


        }
    }

    private void showDialogNoInternet(){
        Dialog dialogNoNetwork = new Dialog(getContext());
        dialogNoNetwork.setCancelable(false);
        dialogNoNetwork.setContentView(R.layout.dialog_no_internet);
        dialogNoNetwork.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnTryAgain = dialogNoNetwork.findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
                dialogNoNetwork.dismiss();

            }
        });

        dialogNoNetwork.show();

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
                            Log.e(TAG, "onAdLoaded: " );
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
        checkNotifications();
        notifyDataSetChanged();
        myAdmobAd.onMainResume();

    }
}