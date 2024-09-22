package com.devm22.happyguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devm22.happyguide.R;
import com.devm22.happyguide.model.Notifications;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsHolder> {
    private static final String TAG = "NotificationsAdapter";

    Context context;

    ArrayList<Notifications> notificationsArrayList = new ArrayList<>();

    private OnItemClickListener mListener;



    public NotificationsAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public void addNotifications(ArrayList<Notifications> notificationsList) {
        notificationsArrayList = notificationsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notify, parent, false);
        return new NotificationsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsHolder holder, int position) {
        Notifications notifications = notificationsArrayList.get(position);

        String notificationLargeIcon = notifications.getNotificationLargeIcon();
        if (notificationLargeIcon != null && !notificationLargeIcon.isEmpty()){
            holder.cardLargeIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(notificationLargeIcon).into(holder.imageLargeIcon);
        }else {
            holder.cardLargeIcon.setVisibility(View.GONE);
        }

        String notificationSmallIcon = notifications.getNotificationIcon();
        if (notificationSmallIcon != null && !notificationSmallIcon.isEmpty()){
            Glide.with(context).load(notificationSmallIcon).into(holder.imageSmallIcon);
        }

        String notificationPicture = notifications.getNotificationPicture();
        if (notificationPicture != null && !notificationPicture.isEmpty()){
            holder.cardPicture.setVisibility(View.VISIBLE);
            Glide.with(context).load(notificationPicture).into(holder.imagePicture);
        }else {
            holder.cardPicture.setVisibility(View.GONE);
        }

        String notificationTitle = notifications.getNotificationTitle();
        if (notificationTitle != null){
            holder.textTitle.setText(notificationTitle);
        }

        String notificationMsg = notifications.getNotificationMessage();
        if (notificationMsg != null){
            holder.textMsg.setText(notificationMsg);
        }

        String notificationDate = notifications.getNotificationSendingDate();
        if (notificationDate != null) {
            holder.textDate.setText(notificationDate);
        }


        holder.itemView.setOnClickListener(view -> {
            mListener.onItemClick(position);
        });


    }

    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }

    public static class NotificationsHolder extends RecyclerView.ViewHolder {
        public ImageView imageLargeIcon, imageSmallIcon, imagePicture;
        public TextView textTitle, textMsg, textDate;
        public CardView cardLargeIcon, cardPicture;
        public NotificationsHolder(@NonNull View itemView) {
            super(itemView);

            imageLargeIcon = itemView.findViewById(R.id.notify_large_icon);
            imageSmallIcon = itemView.findViewById(R.id.notify_small_icon);
            imagePicture = itemView.findViewById(R.id.notify_picture);
            textTitle = itemView.findViewById(R.id.notify_title);
            textMsg = itemView.findViewById(R.id.notify_msg);
            textDate = itemView.findViewById(R.id.notify_date);
            cardLargeIcon = itemView.findViewById(R.id.card_notify_large_icon);
            cardPicture = itemView.findViewById(R.id.card_notify_picture);


        }
    }
}
