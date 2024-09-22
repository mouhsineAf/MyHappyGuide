package com.devm22.happyguide.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.devm22.happyguide.Config;
import com.devm22.happyguide.R;
import com.devm22.happyguide.model.Tips;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TipsAdapter";

    private static final int TYPE_AD = 0;
    private static final int TYPE_NORMAL= 1;
    static int OFFSET = Config.FreePlaceForNativeAd;

    private Context context;

    private ArrayList<Object> mTips = new ArrayList<>();
    private ArrayList<Object> mTipsCopy = new ArrayList<>();

    private ArrayList<NativeAd> adsList = new ArrayList<>();

    private OnItemClickListener mListener;


    public TipsAdapter (Context context) {
        this.context = context;
    }

    public void addTips(ArrayList<Object> tips) {
        this.mTips.clear();
        this.mTipsCopy.clear();
        this.mTips.addAll(tips);
        this.mTipsCopy.addAll(tips);
        notifyDataSetChanged();
    }

    public void addNativeAds(ArrayList<NativeAd> ads) {
        adsList = new ArrayList<>();
        adsList.addAll(ads);
    }

    public void mixNativeWithTips() {
        if(mTips.size()==0) return;
        if(adsList.size()!=0) {

            List<Object> o = new ArrayList<>();
            int num = 0;
            for (int i = 0; i < mTips.size(); i++) {
                if(num+ OFFSET == i ) {
                    //REMOVE +1
                    num += OFFSET;
                    int x = new Random().nextInt(adsList.size());
                    o.add(adsList.get(x));
                    //REMOVE THIS CONTINUE NOT TO SKIP INSERT DATA
                    //continue;

                }
                o.add(mTips.get(i));
            }

            mTips = new ArrayList<>();
            mTips.addAll(o);
        }

        notifyDataSetChanged();
    }

    public int getNumberOfActiveNativeAdd(){
        return adsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onFavoriteClick(Tips tips, int position);
        void onRateChangedClick(int positionRecycle, Tips tips, float rate);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_AD) {
            View unifiedNativeLayoutView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.layout_native_ad_admob_small,
                    parent, false);
            return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tips, parent, false);
        return new TipsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hl, int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_AD) {
            NativeAd nativeAd = (NativeAd) mTips.get(position);
            populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) hl).getAdView());

        }else {

            TipsHolder holder = (TipsHolder) hl;

            Tips currentItem = (Tips) mTips.get(position);
            String imageUrl = currentItem.getTipImage();
            if (!imageUrl.isEmpty()){
                if (imageUrl.contains("https://") || imageUrl.contains("http://")){
                    holder.progressBarLoadingIcon.setVisibility(VISIBLE);
                    Picasso.get()
                            .load(imageUrl)
                            .into(holder.tipsImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.progressBarLoadingIcon.setVisibility(GONE);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });

                }else {
                    holder.tipsImage.setImageResource(Integer.parseInt(imageUrl));
                }
            }

            holder.tipsName.setText(currentItem.getTipName());
            holder.tipsName.setSelected(true);
            holder.tipsName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            //  holder.skinName.setSingleLine(true);


            holder.tipsRate.setRating(currentItem.getTipRate());
            holder.tipsRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    if (b){
                        mListener.onRateChangedClick(position, currentItem, ratingBar.getRating());
                    }
                }
            });


            if (currentItem.isTipFavorite()){
                holder.tipsFavorite.setImageResource(R.drawable.ic_favorite_on);
            }else {
                holder.tipsFavorite.setImageResource(R.drawable.ic_favorite_off);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(currentItem.getTipId());
                }
            });


            holder.tipsFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tips currentItem = (Tips) mTips.get(position);
                    mListener.onFavoriteClick(currentItem, position);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mTips.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTips.get(position) instanceof NativeAd? TYPE_AD: TYPE_NORMAL;
    }


    public void search(String text) {
        mTips.clear();
        if (!text.isEmpty()){
            for (int i=0; i < mTipsCopy.size(); i++){
                Tips tips = (Tips) mTipsCopy.get(i);
                if (tips.getTipName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                    mTips.add(tips);
                }
            }

        }else {
            mTips.addAll(mTipsCopy);
        }
        notifyDataSetChanged();

    }


    public static class TipsHolder extends RecyclerView.ViewHolder {
        public ImageView tipsImage;
        public TextView tipsName;
        public AppCompatRatingBar tipsRate;
        public ImageView tipsFavorite;
        public ProgressBar progressBarLoadingIcon;

        public TipsHolder(@NonNull View itemView) {
            super(itemView);

            tipsImage = itemView.findViewById(R.id.tips_image);
            tipsName = itemView.findViewById(R.id.tips_name);
            tipsRate = itemView.findViewById(R.id.tips_rate);
            tipsFavorite = itemView.findViewById(R.id.tips_favorite);
            progressBarLoadingIcon = itemView.findViewById(R.id.progress_icon_loading);

        }
    }

    public static class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {

        private NativeAdView adView;

        public NativeAdView getAdView() {
            return adView;
        }

        public UnifiedNativeAdViewHolder(View view) {
            super(view);
            adView = (NativeAdView) view.findViewById(R.id.ad_view);

            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
            adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

            // Register the view used for each individual asset.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        }
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

}
